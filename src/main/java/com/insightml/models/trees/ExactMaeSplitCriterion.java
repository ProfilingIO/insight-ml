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

import javax.annotation.Nullable;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.ranking.NaNStrategy;

import com.insightml.math.Vectors;
import com.insightml.math.statistics.IStats;

public final class ExactMaeSplitCriterion implements SplitCriterion {
	private static final Median medianCalculator = new Median().withNaNStrategy(NaNStrategy.FAILED);

	private final int samples;
	private final double totalError;
	private final SplitFinderContext context;
	private final boolean[] subset;

	private final double[] expectedForFeature;

	private ExactMaeSplitCriterion(final int samples, final double totalError, final SplitFinderContext context,
			final boolean[] subset, final double[] expectedForFeature) {
		this.samples = samples;
		this.totalError = totalError;
		this.context = context;
		this.subset = subset;
		this.expectedForFeature = expectedForFeature;
	}

	public static ExactMaeSplitCriterion create(final SplitFinderContext context, final boolean[] subset) {
		// TODO: merge filter step with median calculation, if possible
		final double median = medianCalculator.evaluate(Vectors.filter(context.expected, subset));
		int samples = 0;
		double totalError = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset == null || subset[i]) {
				++samples;
				totalError += Math.abs(context.expected[i] - median) * context.weights[i];
			}
		}
		return new ExactMaeSplitCriterion(samples, totalError, context, subset, null);
	}

	@Override
	public SplitCriterion forFeature(final int feature) {
		final int[] ordered = context.orderedInstances[feature];

		final int bla = ordered.length;

		final double[] values = new double[samples];
		for (int i = 0, insIdx = 0; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}

			values[insIdx] = context.expected[idx];
			++insIdx;
		}
		return new ExactMaeSplitCriterion(samples, totalError, context, subset, values);
	}

	@Override
	public double improvement(final @Nullable IStats sumL, final @Nullable IStats sumNaN, final int feature,
			final int lastIndexLeft) {
		final int[] ordered = context.orderedInstances[feature];

		final int bla = ordered.length;

		int insIdx = 0;
		int splitIndex = -1;

		for (int i = 0; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}

			if (i == lastIndexLeft) {
				splitIndex = insIdx;
				break;
			}
			++insIdx;
		}

		final double medianLeft = medianCalculator.evaluate(expectedForFeature, 0, splitIndex + 1);
		final double medianRight = medianCalculator
				.evaluate(expectedForFeature, splitIndex + 1, samples - splitIndex - 1);

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

		return totalError - error;
	}

	@Override
	public double score(final int feature, final int bestLastIndexLeft, final double bestImprovement) {
		return bestImprovement;
	}

}
