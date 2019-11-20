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

import java.util.concurrent.RecursiveTask;

import com.insightml.math.statistics.MutableStatsBuilder;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.ui.UiUtils;

final class ComparisonFinder extends RecursiveTask<ISplit> {

	private static final long serialVersionUID = -7881568314142752784L;

	private final SplitFinderContext context;
	private final int minObs;
	private final int feature;
	private final double labelSum;
	private final double weightSum;

	public ComparisonFinder(final SplitFinderContext context, final int minObs, final double labelSum,
			final double weightSum, final int feature) {
		this.context = context;
		this.minObs = minObs;
		this.labelSum = labelSum;
		this.weightSum = weightSum;
		this.feature = feature;
	}

	@Override
	public ISplit compute() {
		final Split best = null;

		double bestImprovement = -999;
		int bestFeature2 = -1;

		f: for (int i = 0; i < context.featureNames.length; ++i) {
			if (i != feature) {
				int left = 0;
				final MutableStatsBuilder<Stats> labelSumL = new MutableStatsBuilder<>(new Stats());

				final int length = context.expected.length;
				for (int j = 0; j < length; ++j) {
					if (context.features[j][feature] < context.features[j][i]) {
						if (++left >= length - minObs) {
							continue f;
						}
						labelSumL.add(context.expected[j], context.weights[j]);
					}
				}

				if (left >= minObs) {
					final double improvement = MseSplitCriterion.improvement(labelSumL, null, labelSum, weightSum);
					if (!GrowJob.isFirstBetter(bestImprovement, improvement, i, bestFeature2)) {
						bestImprovement = improvement;
						bestFeature2 = i;
					}
				}
			}
		}
		return best;
	}

	static final class Split extends AbstractSplit {

		private static final long serialVersionUID = 7300516686604742393L;

		private final String featureName;
		private final int featureB;
		private final CharSequence featureNameB;

		public Split(final SplitFinderContext context, final int featureA, final int featureB) {
			super();
			featureName = context.featureNames[featureA];
			this.featureB = featureB;
			featureNameB = context.featureNames[featureB];
		}

		@Override
		public String getFeatureName() {
			return featureName;
		}

		@Override
		public int selectChild(final double[] features) {
			return features[feature] >= features[featureB] ? 1 : 0;
		}

		@Override
		public String explain(final double[] features) {
			if (selectChild(features) == 1) {
				return featureName + " (" + features[feature] + ") >=" + featureNameB + " (" + features[featureB] + ")";
			}
			return featureName + " (" + features[feature] + ") < " + featureNameB + " (" + features[featureB] + ")";
		}

		@Override
		public String toString() {
			return featureName + "<" + featureNameB + " (" + UiUtils.format(improve) + ")";
		}
	}

}
