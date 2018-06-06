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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.stats.FeatureStatistics;
import com.insightml.data.features.stats.IFeatureStatistic;
import com.insightml.data.samples.Sample;

public final class FeatureStatisticFilter extends AbstractFeatureFilter {
	private static final long serialVersionUID = -4528740369747710703L;

	private final Logger logger = LoggerFactory.getLogger(FeatureStatisticFilter.class);

	public <I extends Sample> FeatureStatisticFilter(final IFeatureStatistic stats, final double threshold,
			final boolean threshIsMin, final Iterable<I> instances, final IFeatureProvider<I> provider,
			final int labelIndex) {
		this(stats, threshold, threshIsMin, instances, provider, labelIndex, new HashSet<String>(),
				new HashSet<String>());
	}

	public <I extends Sample> FeatureStatisticFilter(final IFeatureStatistic stats, final double threshold,
			final boolean threshIsMin, final Iterable<I> instances, final IFeatureProvider<I> provider,
			final int labelIndex, final Set<String> keep, final Set<String> ignore) {
		super(keep, ignore);

		final FeatureStatistics stat = new FeatureStatistics(
				PreprocessingPipeline.create(provider, new IgnoreFeatureFilter(), null, null).run(instances, true),
				labelIndex);
		for (final Entry<String, Double> feature : stats.run(stat).entrySet()) {
			if (threshIsMin ? feature.getValue() < threshold : feature.getValue() > threshold) {
				ignoreFeature(feature.getKey());
			}
		}
		logger.info(numIgnored() + " features ignored.");
	}

	@Override
	protected boolean removeFeature(final CharSequence feature) {
		return false;
	}

}
