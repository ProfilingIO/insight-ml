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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
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
		final ISamples<Sample, Double> instances = stats.getInstances();
		final int labelIndex = stats.getLabelIndex();

		return run(instances, labelIndex);
	}

	public static @Nonnull Map<String, Double> run(final ISamples<?, Double> instances, final int labelIndex) {
		final long start = System.currentTimeMillis();
		final ThresholdSplitFinder thresholdSplitFinder = createSplitFinder(instances, labelIndex);

		final String[] feats = instances.featureNames();
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

	private static ThresholdSplitFinder createSplitFinder(final ISamples<?, Double> instances, final int labelIndex) {
		final SplitFinderContext context = new SplitFinderContext(instances, null, 10, 10, labelIndex);
		final boolean[] subset = new boolean[instances.size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		return ThresholdSplitFinder
				.createThresholdSplitFinder(context, subset, MseSplitCriterion::create, () -> new Stats());
	}

	private static double compute(final int feature, final ThresholdSplitFinder thresholdSplitFinder) {
		final ISplit split = thresholdSplitFinder.apply(feature);
		return split == null ? 0 : split.getImprovement() / split.getWeightSum();
	}

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		return UiUtils.toString(Collections.sort(run(instances, labelIndex), SortOrder.DESCENDING), true, false);
	}
}
