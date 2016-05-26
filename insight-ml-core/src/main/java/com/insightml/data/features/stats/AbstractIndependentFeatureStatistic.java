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

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.utils.Arrays;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.Maps;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public abstract class AbstractIndependentFeatureStatistic
		implements IFeatureStatistic, IUiProvider<ISamples<?, Double>> {

	@Override
	public final Map<String, Double> run(final FeatureStatistics stats) {
		final String[] feats = stats.getInstances().featureNames();
		final Double[] result = true ? new Double[feats.length] : Arrays.of(new ParallelFor<Double>() {
			@Override
			protected Double exec(final int i) {
				return Double.valueOf(compute(stats, i, feats[i]));
			}
		}.run(0, feats.length, 1));
		for (int i = 0; i < feats.length; ++i) {
			result[i] = Double.valueOf(compute(stats, i, feats[i]));
		}
		final Map<String, Double> map = Maps.create(feats.length);
		for (int i = 0; i < feats.length; ++i) {
			map.put(feats[i], result[i]);
		}
		return map;
	}

	protected abstract <I extends Sample> double compute(final FeatureStatistics stats, int feature,
			String featureName);

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final FeatureStatistics stats = new FeatureStatistics(instances, labelIndex);
		return UiUtils.toString(Collections.sort(run(stats), SortOrder.DESCENDING), true, false);
	}

}
