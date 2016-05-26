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

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;
import com.insightml.utils.jobs.Threaded;
import com.insightml.utils.ui.reports.IUiProvider;

public final class FeaturesSummary implements IUiProvider<ISamples<?, Double>> {

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final StringBuilder builder = new StringBuilder(1024);
		final FeatureStatistics stats = new FeatureStatistics(instances, labelIndex);
		for (final Pair<String, String> feat : new Threaded<String, String>() {
			@Override
			protected String exec(final int i, final String feature) {
				return stats.toString(feature) + "\n";
			}
		}.run(instances.featureNames(), 1)) {
			builder.append(feat.getSecond());
		}
		return builder.toString();
	}

}
