package com.insightml.models.trees;

import com.insightml.math.statistics.Stats;

public final class MseSplitCriterion implements SplitCriterion {
	private final double weightSum;
	private final double labelSum;

	private MseSplitCriterion(final double weightSum, final double labelSum) {
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
	public double improvement(final Stats sumL, final int feature, final int lastIndexLeft) {
		return improvement(sumL, labelSum, weightSum);
	}

	static final double improvement(final Stats sumL, final double labelSum, final double weightSum) {
		final double labelSumL = sumL.getWeightedSum();
		final double weightSumL = sumL.getSumOfWeights();
		final double weightSumR = weightSum - weightSumL;
		final double dTemp = labelSumL / weightSumL - (labelSum - labelSumL) / weightSumR;
		return weightSumL * weightSumR * dTemp * dTemp / weightSum;
	}
}
