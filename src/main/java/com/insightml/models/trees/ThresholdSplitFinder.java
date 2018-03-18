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
import java.util.function.Supplier;

import com.insightml.math.statistics.IStats;
import com.insightml.math.statistics.MutableStatistics;

public final class ThresholdSplitFinder implements IntFunction<Split> {
	public static final double VALUE_MISSING = Double.NEGATIVE_INFINITY;

	private final SplitFinderContext context;
	private final boolean[] subset;
	private final int samples;
	private final SplitCriterion splitCriterion;
	private final Supplier<MutableStatistics> statisticsFactory;

	public ThresholdSplitFinder(final SplitFinderContext context, final boolean[] subset, final int samples,
			final SplitCriterion splitCriterion, final Supplier<MutableStatistics> statisticsFactory) {
		this.context = context;
		this.subset = subset;
		this.samples = samples;
		this.splitCriterion = splitCriterion;
		this.statisticsFactory = statisticsFactory;
	}

	public static ThresholdSplitFinder createThresholdSplitFinder(final SplitFinderContext context,
			final boolean[] subset, final SplitCriterionFactory splitCriterionFactory,
			final Supplier<MutableStatistics> statisticsFactory) {
		int samples = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset[i]) {
				++samples;
			}
		}
		return new ThresholdSplitFinder(context, subset, samples, splitCriterionFactory.create(context, subset),
				statisticsFactory);
	}

	@Override
	public Split apply(final int feature) {
		double curThr = -9999999;
		final int[] ordered = context.orderedInstances[feature];

		final SplitCriterion localCriterion = splitCriterion.forFeature(feature);

		IStats bestSplitL = null;
		double bestThreshold = 0;
		double bestImprovement = context.minImprovement - 0.000000001;
		int bestLastIndexLeft = -1;

		final MutableStatistics currentSplitL = statisticsFactory.get();
		MutableStatistics statsNaN = statisticsFactory.get();
		int lastIndexNaN = -1;

		final int max = samples - context.minObs;
		final int bla = ordered.length;

		for (int i = 0; i < -bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}
			final double value = context.features[idx][feature];
			if (value == VALUE_MISSING) {
				statsNaN.add(context.expected[idx], context.weights[idx]);
				lastIndexNaN = i;
			} else {
				break;
			}
		}

		// there are too few observations of missing values, or too many such no
		// further split can be made
		// TODO: also allow missing vs non-missing splits
		if (lastIndexNaN + 1 < context.minObs || lastIndexNaN + 1 > samples - context.minObs * 2) {
			statsNaN = statisticsFactory.get();
			lastIndexNaN = -1;
			// if there are not enough observations of missing values, count
			// them to the left subtree
		}

		int left = 0;
		int seen = lastIndexNaN + 1;

		for (int i = lastIndexNaN + 1; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}
			final double value = context.features[idx][feature];
			if (left >= context.minObs && value != curThr) {
				final double improvement = localCriterion.improvement(currentSplitL, statsNaN, feature, i - 1);
				if (improvement > bestImprovement) {
					bestSplitL = currentSplitL.copy();
					bestThreshold = curThr;
					bestImprovement = improvement;
					bestLastIndexLeft = i - 1;
				}
			}
			currentSplitL.add(context.expected[idx], context.weights[idx]);
			curThr = value;
			++left;
			if (seen++ == max) {
				break;
			}
		}
		if (bestLastIndexLeft == -1) {
			return null;
		}
		final IStats statsR = createStatsRight(ordered, bestLastIndexLeft);
		final double score = localCriterion.score(feature, bestLastIndexLeft, bestImprovement);
		return new Split(bestThreshold, bestSplitL, statsR, statsNaN, score, lastIndexNaN, bestLastIndexLeft, feature,
				context.featureNames);
	}

	private IStats createStatsRight(final int[] ordered, final int bestLastIndexLeft) {
		final MutableStatistics statsR = statisticsFactory.get();
		final int bla = ordered.length;
		for (int i = bestLastIndexLeft + 1; i < bla; ++i) {
			final int idx = ordered[i];
			if (!subset[idx]) {
				continue;
			}
			statsR.add(context.expected[idx], context.weights[idx]);
		}
		return statsR;
	}

}
