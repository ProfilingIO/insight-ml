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

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.math.Vectors;
import com.insightml.math.statistics.Stats;
import com.insightml.models.AbstractDoubleLearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Arguments;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.Pair;
import com.insightml.utils.jobs.ParallelFor;

public final class RegTree extends AbstractDoubleLearner<Double> {

	public RegTree(final IArguments arguments) {
		super(arguments);
	}

	public RegTree(final int depth, final int minobs) {
		super(new Arguments("depth", depth + "", "minObs", minobs + ""));
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("depth", 4.0, 1, 24);
		args.add("minObs", 10.0, 1, 20000);
		return args;
	}

	@Override
	public TreeModel run(final LearnerInput<? extends ISample, ? extends Double, ?> input) {
		Check.state(input.valid == null);
		final ISamples<ISample, Double> train = (ISamples<ISample, Double>) input.getTrain();
		final Stats sRoot = new Stats();
		sRoot.add(0, Vectors.sum(train.weights(input.labelIndex)));
		final TreeNode root = new TreeNode(sRoot);
		final SplitFinderContext context = new SplitFinderContext(train, (int) argument("depth"),
				(int) argument("minObs"), input.labelIndex);
		final boolean[] subset = new boolean[train.size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		new GrowJob(root, context, subset, 1).compute();
		return new TreeModel(root, train.featureNames());
	}

	static final class GrowJob extends RecursiveAction {

		private static final long serialVersionUID = 1788913869138107684L;

		private final TreeNode parent;
		final SplitFinderContext context;
		final boolean[] subset;
		private final int depth;

		public GrowJob(final TreeNode left, final SplitFinderContext context, final boolean[] subset, final int depth) {
			parent = left;
			this.context = context;
			this.subset = subset;
			this.depth = depth;
		}

		@Override
		protected void compute() {
			final ISplit best = findBestSplit();
			// TODO: Do crossval here to reject split, if necessary
			if (best == null || best.getImprovement() < 0.00000000001) {
				return;
			}
			final TreeNode left = new TreeNode(best.getStatsL());
			final TreeNode right = new TreeNode(best.getStatsR());
			final Pair<boolean[], boolean[]> split = parent.split(best, left, right, context.orderedInstances, subset);
			if (depth < context.maxDepth) {
				if (split.getFirst().length >= context.minObs * 2) {
					new GrowJob(left, context, split.getFirst(), depth + 1).compute();
				}
				if (split.getSecond().length >= context.minObs * 2) {
					new GrowJob(right, context, split.getSecond(), depth + 1).compute();
				}
			} else if (false) {
				final AbstractDoubleLearner learner = null;
				// left.setLeafModel(learner, split.getFirst());
				// right.setLeafModel(learner, split.getSecond());
			}
		}

		private ISplit findBestSplit() {
			int samples = 0;
			double weightSum = 0;
			double labelSum = 0;
			for (int i = 0; i < context.weights.length; ++i) {
				if (subset[i]) {
					++samples;
					weightSum += context.weights[i];
					labelSum += context.expected[i] * context.weights[i];
				}
			}

			final int samplesF = samples;
			final double labelSumF = labelSum;
			final double weightSumF = weightSum;

			ISplit bestSplit = null;
			for (final ISplit split : new ParallelFor<ISplit>() {
				@Override
				protected ISplit exec(final int i) {
					return new ThresholdSplitFinder(context, subset, samplesF, labelSumF, weightSumF, i).compute();
				}
			}.run(0, context.orderedInstances.length, 1)) {
				if (split != null && (bestSplit == null || split.isBetterThan(bestSplit))) {
					bestSplit = split;
				}
			}
			return bestSplit;
		}
	}

}
