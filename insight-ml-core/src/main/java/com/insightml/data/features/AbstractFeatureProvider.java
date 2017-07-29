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
package com.insightml.data.features;

import java.util.Map;

import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractModule;

public abstract class AbstractFeatureProvider<I extends Sample> extends AbstractModule implements IFeatureProvider<I> {

	protected double defaultValue;

	protected AbstractFeatureProvider() {
	}

	public AbstractFeatureProvider(final String name, final double defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}

	@Override
	public final double[] features(final I instance, final CharSequence[] features,
			final Map<String, Stats> featureStats, final boolean isTraining) {
		final double[] array = new double[Check.size(features, 1, 5000).length];
		final Map<String, Double> feats = features(instance, isTraining).asMap();
		for (int i = 0; i < features.length; ++i) {
			@SuppressWarnings("unlikely-arg-type")
			final Double feat = feats.get(features[i]);
			array[i] = feat == null ? handleMissingValue((String) features[i], featureStats) : feat.doubleValue();
		}
		return array;
	}

	@SuppressWarnings("unused")
	protected double handleMissingValue(final String featureName, final Map<String, Stats> featureStats) {
		return defaultValue;
	}

	@Override
	public String getReport() {
		throw new IllegalAccessError();
	}
}
