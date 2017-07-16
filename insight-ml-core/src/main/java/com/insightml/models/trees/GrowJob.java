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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.math3.util.Pair;

import com.insightml.models.AbstractDoubleLearner;
import com.insightml.utils.ResourceCloser;
import com.insightml.utils.jobs.ParallelFor;

final class GrowJob extends RecursiveAction {
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
	private final boolean parallelize;

	public GrowJob(final TreeNode left, final SplitFinderContext context, final boolean[] subset, final int depth,
			final boolean parallelize) {
		parent = left;
		this.context = context;
		this.subset = subset;
		this.depth = depth;
		this.parallelize = parallelize;
	}

	@Override
	protected void compute() {
		final Split best = findBestSplit();
		// TODO: Do crossval here to reject split, if necessary
		if (best == null || best.getImprovement() < 0.00000000001) {
			return;
		}
		final TreeNode left = new TreeNode(best.getStatsL());
		final TreeNode right = new TreeNode(best.getStatsR());
		final Pair<boolean[], boolean[]> split = parent.split(best, left, right, context.orderedInstances, subset);
		if (depth < context.maxDepth) {
			if (split.getFirst().length >= context.minObs * 2) {
				new GrowJob(left, context, split.getFirst(), depth + 1, parallelize).compute();
			}
			if (split.getSecond().length >= context.minObs * 2) {
				new GrowJob(right, context, split.getSecond(), depth + 1, parallelize).compute();
			}
		} else if (false) {
			final AbstractDoubleLearner learner = null;
			// left.setLeafModel(learner, split.getFirst());
			// right.setLeafModel(learner, split.getSecond());
		}
	}

	private Split findBestSplit() {
		final ThresholdSplitFinder thresholdSplitFinder = RegTree.createThresholdSplitFinder(context, subset);
		return parallelize ? findBestSplitParallel(thresholdSplitFinder) : findBestSplit(thresholdSplitFinder);
	}

	private Split findBestSplit(final ThresholdSplitFinder thresholdSplitFinder) {
		Split bestSplit = null;
		for (int i = 0; i < context.orderedInstances.length; ++i) {
			final Split split = thresholdSplitFinder.apply(i);
			if (split != null && (bestSplit == null || split.isBetterThan(bestSplit))) {
				bestSplit = split;
			}
		}
		return bestSplit;
	}

	private Split findBestSplitParallel(final ThresholdSplitFinder thresholdSplitFinder) {
		Split bestSplit = null;
		for (final Split split : ParallelFor
				.run(thresholdSplitFinder, 0, context.orderedInstances.length, 1, executor)) {
			if (split != null && (bestSplit == null || split.isBetterThan(bestSplit))) {
				bestSplit = split;
			}
		}
		return bestSplit;
	}
}