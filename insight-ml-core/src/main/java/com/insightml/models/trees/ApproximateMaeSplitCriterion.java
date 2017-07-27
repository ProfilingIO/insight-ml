package com.insightml.models.trees;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.ranking.NaNStrategy;

import com.insightml.math.Vectors;
import com.insightml.math.statistics.IStats;

public class ApproximateMaeSplitCriterion implements SplitCriterion {
	private static final Median medianCalculator = new Median().withNaNStrategy(NaNStrategy.FAILED);

	private final SplitFinderContext context;
	private final boolean[] subset;
	private final int samples;
	private final MseSplitCriterion mseSplitCriterion;

	public ApproximateMaeSplitCriterion(final SplitFinderContext context, final boolean[] subset,
			final MseSplitCriterion mseSplitCriterion) {
		this.context = context;
		this.subset = subset;
		samples = Vectors.sum(subset);
		this.mseSplitCriterion = mseSplitCriterion;
	}

	public static ApproximateMaeSplitCriterion create(final SplitFinderContext context, final boolean[] subset) {
		return new ApproximateMaeSplitCriterion(context, subset, MseSplitCriterion.create(context, subset));
	}

	@Override
	public double improvement(final IStats sumL, final IStats sumNaN, final int featureIndex, final int lastIndexLeft) {
		return mseSplitCriterion.improvement(sumL, sumNaN, featureIndex, lastIndexLeft);
	}

	@Override
	public SplitCriterion forFeature(final int feature) {
		return this;
	}

	@Override
	public double score(final int feature, final int lastIndexLeft, final double bestImprovement) {
		final int[] ordered = context.orderedInstances[feature];

		final int bla = ordered.length;

		final double[] values = new double[samples];
		int insIdx = 0;
		int splitIndex = -1;

		for (int i = 0; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}

			values[insIdx] = context.expected[idx];

			if (i == lastIndexLeft) {
				splitIndex = insIdx;
				break;
			}
			++insIdx;
		}

		final double medianLeft = medianCalculator.evaluate(values, 0, splitIndex + 1);
		final double medianRight = medianCalculator.evaluate(values, splitIndex + 1, samples - splitIndex - 1);

		double error = 0;
		for (int i = 0; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}

			if (i <= lastIndexLeft) {
				error += Math.abs(medianLeft - context.expected[idx]) * context.weights[idx];
			} else {
				error += Math.abs(medianRight - context.expected[idx]) * context.weights[idx];
			}
		}
		return 99999999 - error;
	}

}
