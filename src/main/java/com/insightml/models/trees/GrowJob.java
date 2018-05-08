/*
 * Copyright (C) 2016 Stefan Henß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightml.models.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;
import java.util.function.Supplier;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.math.statistics.IStats;
import com.insightml.math.statistics.MutableStatistics;
import com.insightml.utils.ResourceCloser;
import com.insightml.utils.jobs.JobPool;

public final class GrowJob extends RecursiveAction {
	private static final long serialVersionUID = 1788913869138107684L;

	private static final ExecutorService executor;

	static {
		executor = Executors.newFixedThreadPool(16);
		ResourceCloser.register(executor::shutdown);
	}

	private final TreeNode parent;
	final SplitFinderContext context;
	final boolean[] subset;
	private final int depth;
	private final String nodePrediction;
	private final SplitCriterionFactory splitCriterionFactory;
	private final boolean parallelize;
	private final Supplier<MutableStatistics> statisticsFactory;

	public GrowJob(final TreeNode left, final SplitFinderContext context, final boolean[] subset, final int depth,
			final String nodePrediction, final SplitCriterionFactory splitCriterionFactory,
			final Supplier<MutableStatistics> statisticsFactory, final boolean parallelize) {
		parent = left;
		this.context = context;
		this.subset = subset;
		this.depth = depth;
		this.nodePrediction = nodePrediction;
		this.splitCriterionFactory = splitCriterionFactory;
		this.statisticsFactory = statisticsFactory;
		this.parallelize = parallelize;
	}

	@Override
	public void compute() {
		final Split best = findBestSplit();
		// TODO: Do crossval here to reject split, if necessary
		if (best == null || best.getImprovement() < 0.00000000001) {
			return;
		}
		final DescriptiveStatistics[] nodeStats = nodeStats(best);
		final IStats statsNaN = best.getStatsNaN();
		final TreeNode[] children = new TreeNode[statsNaN.getN() >= context.minObs ? 3 : 2];
		children[0] = new TreeNode(prediction(nodeStats[0]), best.getStatsL());
		children[1] = new TreeNode(prediction(nodeStats[1]), best.getStatsR());
		if (children.length == 3) {
			children[2] = new TreeNode(prediction(nodeStats[2]), statsNaN);
		}
		final boolean[][] split = parent.split(best, children, context.orderedInstances, subset);
		if (depth < context.maxDepth) {
			for (int i = 0; i < children.length; ++i) {
				if (split[i].length >= context.minObs * 2) {
					new GrowJob(children[i], context, split[i], depth + 1, nodePrediction, splitCriterionFactory,
							statisticsFactory, parallelize).compute();
				}
			}
		}
	}

	private double prediction(final DescriptiveStatistics stats) {
		switch (nodePrediction) {
		case "mean":
			return stats.getMean();
		case "median":
			return stats.getPercentile(50);
		case "meandian":
			return (stats.getMean() + stats.getPercentile(50)) / 2;
		default:
			throw new IllegalArgumentException(nodePrediction);
		}
	}

	private DescriptiveStatistics[] nodeStats(final Split best) {
		final double[] expected = context.expected;
		final DescriptiveStatistics[] stats = new DescriptiveStatistics[3];
		for (int i = 0; i < stats.length; ++i) {
			stats[i] = new DescriptiveStatistics();
		}
		for (int i = 0; i < expected.length; ++i) {
			if (!subset[i]) {
				continue;
			}
			stats[best.selectChild(context.features[i])].addValue(expected[i]);
		}
		return stats;
	}

	private Split findBestSplit() {
		final ThresholdSplitFinder thresholdSplitFinder = ThresholdSplitFinder
				.createThresholdSplitFinder(context, subset, splitCriterionFactory, statisticsFactory);
		return parallelize ? findBestSplitParallel(thresholdSplitFinder) : findBestSplit(thresholdSplitFinder);
	}

	private Split findBestSplit(final ThresholdSplitFinder thresholdSplitFinder) {
		Split bestSplit = null;
		final boolean[] featuresMask = context.featuresMask;
		for (int i = 0; i < context.orderedInstances.length; ++i) {
			if (featuresMask != null && !featuresMask[i]) {
				continue;
			}
			final Split split = thresholdSplitFinder.apply(i);
			if (split == null) {
				continue;
			}
			if (bestSplit == null
					|| GrowJob.isFirstBetter(split.improve, bestSplit.improve, split.feature, bestSplit.feature)) {
				bestSplit = split;
			}
		}
		return bestSplit;
	}

	private Split findBestSplitParallel(final ThresholdSplitFinder thresholdSplitFinder) {
		Split bestSplit = null;
		final boolean[] featuresMask = context.featuresMask;
		final List<Callable<Split>> tasks = new ArrayList<>();
		for (int i = 0; i < context.orderedInstances.length; ++i) {
			if (featuresMask == null || featuresMask[i]) {
				final int idx = i;
				tasks.add(() -> thresholdSplitFinder.apply(idx));
			}
		}
		for (final Split split : JobPool.execute(tasks, 1, executor)) {
			if (split == null) {
				continue;
			}
			if (bestSplit == null
					|| GrowJob.isFirstBetter(split.improve, bestSplit.improve, split.feature, bestSplit.feature)) {
				bestSplit = split;
			}
		}
		return bestSplit;
	}

	public static boolean isFirstBetter(final double score1, final double score2, final int feature1,
			final int feature2) {
		if (score1 < score2) {
			return false;
		}
		if (score1 > score2) {
			return true;
		}
		return feature1 == feature2 ? true : feature1 < feature2;
	}
}