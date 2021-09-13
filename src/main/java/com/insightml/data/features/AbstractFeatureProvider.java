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
import com.insightml.utils.IArguments;

public abstract class AbstractFeatureProvider<I extends Sample> implements IFeatureProvider<I> {

	private String name;
	protected float defaultValue;

	protected AbstractFeatureProvider() {
	}

	public AbstractFeatureProvider(final String name, final float defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName(final IArguments arguments) {
		return name;
	}

	@Override
	public final float[] features(final I instance, final CharSequence[] features,
			final Map<String, Stats> featureStats, final boolean isTraining, final IArguments arguments) {
		final float[] array = new float[Check.size(features, 1, 5000).length];
		final FeaturesMapBuilder feats = new FeaturesMapBuilder();
		features(instance, isTraining, arguments, feats);
		for (int i = 0; i < features.length; ++i) {
			final Double feat = feats.get(features[i]);
			array[i] = feat == null ? handleMissingValue((String) features[i], featureStats) : feat.floatValue();
		}
		return array;
	}

	/**
	 * @param featureName
	 * @param featureStats
	 */
	protected float handleMissingValue(final String featureName, final Map<String, Stats> featureStats) {
		return defaultValue;
	}

	@Override
	public String getReport() {
		throw new IllegalAccessError();
	}
}
