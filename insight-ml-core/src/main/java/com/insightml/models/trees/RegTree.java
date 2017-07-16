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

import java.util.concurrent.RecursiveAction;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.Vectors;
import com.insightml.math.statistics.Stats;
import com.insightml.models.AbstractDoubleLearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Arguments;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.jobs.ParallelFor;

public final class RegTree extends AbstractDoubleLearner<Double> {
	private final boolean parallelize;

	public RegTree(final IArguments arguments) {
		super(arguments);
		parallelize = true;
	}

	public RegTree(final int depth, final int minobs, final boolean parallelize) {
		super(new Arguments("depth", String.valueOf(depth), "minObs", String.valueOf(minobs)));
		this.parallelize = parallelize;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("depth", 4.0, 1, 24);
		args.add("minObs", 10.0, 1, 20000);
		return args;
	}

	@Override
	public TreeModel run(final LearnerInput<? extends Sample, ? extends Double> input) {
		Check.state(input.valid == null);
		final ISamples<Sample, Double> train = (ISamples<Sample, Double>) input.getTrain();
		final Stats sRoot = new Stats();
		sRoot.add(0, Vectors.sum(train.weights(input.labelIndex)));
		final TreeNode root = new TreeNode(sRoot);
		final SplitFinderContext context = new SplitFinderContext(train, (int) argument("depth"),
				(int) argument("minObs"), input.labelIndex);
		final boolean[] subset = new boolean[train.size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		new GrowJob(root, context, subset, 1, parallelize).compute();
		return new TreeModel(root, train.featureNames());
	}

	public static ThresholdSplitFinder createThresholdSplitFinder(final SplitFinderContext context,
			final boolean[] subset) {
		int samples = 0;
		double weightSum = 0;
		double labelSum = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset == null || subset[i]) {
				++samples;
				weightSum += context.weights[i];
				labelSum += context.expected[i] * context.weights[i];
			}
		}
		return new ThresholdSplitFinder(context, subset, samples, labelSum, weightSum);
	}

	static final class GrowJob extends RecursiveAction {
		private static final long serialVersionUID = 1788913869138107684L;

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
			final ThresholdSplitFinder thresholdSplitFinder = createThresholdSplitFinder(context, subset);
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
			for (final Split split : ParallelFor.run(thresholdSplitFinder, 0, context.orderedInstances.length, 1)) {
				if (split != null && (bestSplit == null || split.isBetterThan(bestSplit))) {
					bestSplit = split;
				}
			}
			return bestSplit;
		}
	}

}
