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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISamples;
import com.insightml.utils.Arrays;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.Maps;
import com.insightml.utils.ProgressMonitor;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public abstract class AbstractIndependentFeatureStatistic
		implements IFeatureStatistic, IUiProvider<ISamples<?, Double>> {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Override
	public final Map<String, Double> run(final FeatureStatistics stats) {
		final long start = System.currentTimeMillis();
		final String[] feats = stats.getInstances().featureNames();
		final ProgressMonitor progressMonitor = new ProgressMonitor(feats.length, 50);
		final Double[] result = Arrays.of(ParallelFor.run(i -> {
			final double value = compute(stats, i, feats[i]);
			progressMonitor.tick();
			return Double.valueOf(value);
		}, 0, feats.length, 1));
		final Map<String, Double> map = Maps.create(feats.length);
		for (int i = 0; i < feats.length; ++i) {
			map.put(feats[i], result[i]);
		}
		LOG.info("Computed statistics in {} ms", Long.valueOf(System.currentTimeMillis() - start));
		return map;
	}

	protected abstract double compute(final FeatureStatistics stats, int feature, String featureName);

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final FeatureStatistics stats = new FeatureStatistics(instances, labelIndex);
		return UiUtils.toString(Collections.sort(run(stats), SortOrder.DESCENDING), true, false);
	}

}
