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
import java.util.Map.Entry;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.insightml.data.samples.ISamples;
import com.insightml.utils.ui.SimpleFormatter;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public final class FeaturesImportance implements IUiProvider<ISamples<?, Double>> {

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final FeatureStatistics stats = new FeatureStatistics(instances, labelIndex);
		final Map<String, Double> mi = new MutualInformation(0.1).run(stats);
		final Map<String, Double> chi = new ChiSquare(0.1).run(stats);
		final StringBuilder builder = new StringBuilder(1024);
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		for (final Entry<String, Double> feature : ImmutableSortedMap
				.copyOf(mi, Ordering.natural().reverse().onResultOf(Functions.forMap(mi))).entrySet()) {
			builder.append(UiUtils.fill(feature.getKey(), 25) + "\t");
			builder.append("MutualInformation: " + UiUtils.fill(formatter.format(feature.getValue()), 12));
			builder.append("ChiSquare: " + UiUtils.fill(formatter.format(chi.get(feature.getKey())), 12));
			builder.append("\n");
		}
		return builder.toString();
	}

}
