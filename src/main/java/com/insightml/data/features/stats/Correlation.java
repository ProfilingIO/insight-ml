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

import com.insightml.data.features.stats.FeaturesCorrelation.FeatureCorrelation;
import com.insightml.utils.Maps;

public final class Correlation implements IFeatureStatistic {

	@Override
	public Map<String, Double> run(final FeatureStatistics stats) {
		final FeatureCorrelation[] corr = FeaturesCorrelation.correlation(stats.getInstances(), stats.getLabelIndex());
		final Map<String, Double> result = Maps.create(corr.length);
		for (final FeatureCorrelation entry : corr) {
			result.put(entry.getFeature(), entry.getMean());
		}
		return result;
	}

}
