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

import java.util.Arrays;
import java.util.stream.Collectors;

import com.insightml.data.samples.ISamples;
import com.insightml.utils.ui.reports.IUiProvider;

public final class FeaturesSummary implements IUiProvider<ISamples<?, Double>> {

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final FeatureStatistics stats = new FeatureStatistics(instances, labelIndex);
		return Arrays.stream(instances.featureNames()).parallel().map(stats::toString).sorted()
				.collect(Collectors.joining("\n"));
	}

}
