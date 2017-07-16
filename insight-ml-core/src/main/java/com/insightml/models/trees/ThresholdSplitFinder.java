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

import java.util.function.IntFunction;

import com.insightml.math.statistics.Stats;
import com.insightml.utils.Check;

public final class ThresholdSplitFinder implements IntFunction<Split> {
	private final SplitFinderContext context;
	private final boolean[] subset;
	private final int samples;
	private final double labelSum;
	private final double weightSum;

	public ThresholdSplitFinder(final SplitFinderContext context, final boolean[] subset, final int samples,
			final double labelSum, final double weightSum) {
		this.context = context;
		this.subset = subset;
		this.samples = samples;
		this.labelSum = labelSum;
		this.weightSum = weightSum;
	}

	public double getLabelSum() {
		return labelSum;
	}

	public double getWeightSum() {
		return weightSum;
	}

	@Override
	public Split apply(final int feature) {
		double curThr = -9999999;
		final int[] ordered = context.orderedInstances[feature];

		Stats bestSplitL = null;
		double bestThreshold = 0;
		double bestImprovement = -999;
		int bestLastIndexLeft = -1;

		int left = 0;
		final Stats currentSplitL = new Stats();

		final int max = samples - context.minObs;
		final int bla = ordered.length;
		for (int i = 0; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}
			final double value = context.features[idx][feature];
			if (left >= context.minObs && value != curThr) {
				final double improvement = AbstractSplit.improvement(currentSplitL, labelSum, weightSum);
				if (!AbstractSplit.isFirstBetter(bestImprovement, improvement, feature, feature)) {
					bestSplitL = currentSplitL.copy();
					bestThreshold = curThr;
					bestImprovement = improvement;
					bestLastIndexLeft = i - 1;
				}
			}
			currentSplitL.add(context.expected[idx], context.weights[idx]);
			curThr = value;
			if (left++ == max) {
				break;
			}
		}
		if (bestLastIndexLeft == -1) {
			return null;
		}
		final Stats statsR = new Stats();
		for (int i = bestLastIndexLeft + 1; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}
			statsR.add(context.expected[idx], context.weights[idx]);
		}
		if (false) {
			Check.equals(bestSplitL.getSumOfWeights() + statsR.getSumOfWeights(), weightSum, "weight sum");
		}
		return new Split(bestThreshold, bestSplitL, statsR, bestImprovement, bestLastIndexLeft, feature,
				context.featureNames);
	}

}
