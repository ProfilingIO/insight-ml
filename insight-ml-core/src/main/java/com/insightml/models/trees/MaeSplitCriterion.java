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

import com.insightml.math.statistics.Stats;

/**
 * Quite naive implementation. Should be revised soon.
 */
public final class MaeSplitCriterion implements SplitCriterion {
	private final double totalError;
	private final SplitFinderContext context;
	private final boolean[] subset;

	private MaeSplitCriterion(final double totalError, final SplitFinderContext context, final boolean[] subset) {
		this.totalError = totalError;
		this.context = context;
		this.subset = subset;
	}

	public static MaeSplitCriterion create(final SplitFinderContext context, final boolean[] subset) {
		double weightSum = 0;
		double labelSum = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset == null || subset[i]) {
				weightSum += context.weights[i];
				labelSum += context.expected[i] * context.weights[i];
			}
		}
		final double mean = labelSum / weightSum;
		double totalError = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset == null || subset[i]) {
				totalError += Math.abs(context.expected[i] - mean) * context.weights[i];
			}
		}
		return new MaeSplitCriterion(totalError, context, subset);
	}

	@Override
	public double improvement(final Stats sumL, final int feature, final int lastIndexLeft) {
		final int[] ordered = context.orderedInstances[feature];

		final double meanLeft = sumL.getMean();

		final int bla = ordered.length;
		double labelSumR = 0;
		double weightSumR = 0;
		for (int i = lastIndexLeft + 1; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}
			labelSumR += context.expected[idx] * context.weights[idx];
			weightSumR += context.expected[idx] * context.weights[idx];
		}
		final double meanRight = labelSumR / weightSumR;

		double error = 0;
		for (int i = 0; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}

			if (i <= lastIndexLeft) {
				error += Math.abs(meanLeft - context.expected[idx]) * context.weights[idx];
			} else {
				error += Math.abs(meanRight - context.expected[idx]) * context.weights[idx];
			}
		}

		return totalError - error;
	}

}
