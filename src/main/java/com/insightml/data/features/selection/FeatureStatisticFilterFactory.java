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
package com.insightml.data.features.selection;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.stats.FeatureStatistics;
import com.insightml.data.features.stats.IFeatureStatistic;
import com.insightml.data.samples.Sample;
import com.insightml.utils.Collections;

public final class FeatureStatisticFilterFactory implements FeatureFilterFactory {
	private final IFeatureStatistic stats;
	private final double threshold;
	private final boolean threshIsMin;
	private final int getTopNFeatures;

	public FeatureStatisticFilterFactory(final IFeatureStatistic stats, final double threshold,
			final boolean threshIsMin) {
		this(stats, threshold, threshIsMin, 100_000);
	}

	public FeatureStatisticFilterFactory(final IFeatureStatistic stats, final double threshold,
			final boolean threshIsMin, final int getTopNFeatures) {
		this.stats = stats;
		this.threshold = threshold;
		this.threshIsMin = threshIsMin;
		this.getTopNFeatures = getTopNFeatures;
	}

	@Override
	public <I extends Sample> IFeatureFilter createFilter(final Iterable<I> instances,
			final IFeatureProvider<I> provider, final int labelIndex) {
		final FeatureStatistics stat = new FeatureStatistics(
				PreprocessingPipeline.create(provider, new IgnoreFeatureFilter(), null, null).run(instances, true),
				labelIndex);

		// FIXME: also support the "getTopNFeatures" parameter when "threshIsMin" is
		// false
		if (!threshIsMin) {
			final Set<String> ignoredFeatures = new HashSet<>();
			for (final Entry<String, Double> feature : stats.run(stat).entrySet()) {
				if (threshIsMin ? feature.getValue() < threshold : feature.getValue() > threshold) {
					ignoredFeatures.add(feature.getKey());
				}
			}
			return new ManualSelectionFilter(ignoredFeatures, false);
		}
		return new ManualSelectionFilter(Collections.getTopN(stats.run(stat), getTopNFeatures, threshold).keySet(),
				true);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stats, threshold, threshIsMin, getTopNFeatures);
	}
}