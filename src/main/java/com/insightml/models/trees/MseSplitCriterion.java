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

import java.io.Serializable;

import com.insightml.math.statistics.StatsBuilder;

public final class MseSplitCriterion implements SplitCriterion {
	private final double weightSum;
	private final double labelSum;

	MseSplitCriterion(final double weightSum, final double labelSum) {
		this.weightSum = weightSum;
		this.labelSum = labelSum;
	}

	public static MseSplitCriterion create(final SplitFinderContext context, final boolean[] subset) {
		double weightSum = 0;
		double labelSum = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset == null || subset[i]) {
				weightSum += context.weights[i];
				labelSum += context.expected[i] * context.weights[i];
			}
		}
		return new MseSplitCriterion(weightSum, labelSum);
	}

	@Override
	public SplitCriterion forFeature(final int feature) {
		return this;
	}

	@Override
	public double improvement(final StatsBuilder<?> sumL, final StatsBuilder<?> sumNaN, final int feature,
			final int lastIndexLeft) {
		return improvement(sumL, sumNaN, labelSum, weightSum);
	}

	static double improvement(final StatsBuilder<?> sumL, final StatsBuilder<?> sumNaN, final double labelSum,
			final double weightSum) {
		final double labelSumL = sumL.getWeightedSum();
		final double weightSumL = sumL.getSumOfWeights();

		final double labelSumNaN = sumNaN.getWeightedSum();
		final double weightSumNaN = sumNaN.getSumOfWeights();

		final double weightSumR = weightSum - weightSumL - weightSumNaN;
		final double labelSumR = labelSum - labelSumL - labelSumNaN;

		final double dTemp = labelSumL / weightSumL - (labelSumNaN == 0 ? 0 : labelSumNaN / weightSumNaN)
				- labelSumR / weightSumR;
		return weightSumL * weightSumR * (weightSumNaN > 0 ? weightSumNaN : 1) * dTemp * dTemp / weightSum;
	}

	@Override
	public double score(final int feature, final int bestLastIndexLeft, final double bestImprovement) {
		return bestImprovement;
	}

	public static MseSplitCriterionFactory factory() {
		return new MseSplitCriterionFactory();
	}

	public static final class MseSplitCriterionFactory implements SplitCriterionFactory, Serializable {
		private static final long serialVersionUID = 2681661396690754579L;

		@Override
		public SplitCriterion create(final SplitFinderContext context, final boolean[] subset) {
			return MseSplitCriterion.create(context, subset);
		}
	}
}
