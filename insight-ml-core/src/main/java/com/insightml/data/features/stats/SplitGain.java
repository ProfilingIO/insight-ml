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
package com.insightml.data.features.stats;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISamples;
import com.insightml.models.trees.ISplit;
import com.insightml.models.trees.MseSplitCriterion;
import com.insightml.models.trees.SplitFinderContext;
import com.insightml.models.trees.ThresholdSplitFinder;
import com.insightml.utils.Arrays;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public final class SplitGain implements IFeatureStatistic, IUiProvider<ISamples<?, Double>> {
	private static final Logger LOG = LoggerFactory.getLogger(SplitGain.class);

	@Override
	public final Map<String, Double> run(final FeatureStatistics stats) {
		final long start = System.currentTimeMillis();
		final ThresholdSplitFinder thresholdSplitFinder = createSplitFinder(stats);

		final String[] feats = stats.getInstances().featureNames();
		final Double[] result = Arrays.of(ParallelFor.run(i -> {
			return Double.valueOf(compute(i, thresholdSplitFinder));
		}, 0, feats.length, 1));
		final Map<String, Double> map = new HashMap<>(feats.length);
		for (int i = 0; i < feats.length; ++i) {
			map.put(feats[i], result[i]);
		}

		LOG.info("Computed statistics in {} ms", Long.valueOf(System.currentTimeMillis() - start));
		return map;
	}

	private static ThresholdSplitFinder createSplitFinder(final FeatureStatistics stats) {
		final SplitFinderContext context = new SplitFinderContext(stats.getInstances(), 10, 10, stats.getLabelIndex());
		final boolean[] subset = new boolean[stats.getInstances().size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		return ThresholdSplitFinder.createThresholdSplitFinder(context, subset, MseSplitCriterion::create);
	}

	private static double compute(final int feature, final ThresholdSplitFinder thresholdSplitFinder) {
		final ISplit split = thresholdSplitFinder.apply(feature);
		return split == null ? 0 : split.getImprovement() / split.getWeightSum();
	}

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final FeatureStatistics stats = new FeatureStatistics(instances, labelIndex);
		return UiUtils.toString(Collections.sort(run(stats), SortOrder.DESCENDING), true, false);
	}
}
