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

import com.insightml.data.samples.ISample;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractConfigurable;
import com.insightml.utils.types.Parameter;

public abstract class AbstractFeatureProvider<I extends ISample> extends AbstractConfigurable
		implements IFeatureProvider<I> {

	private double defaultValue;

	protected AbstractFeatureProvider() {
	}

	public AbstractFeatureProvider(final String name, final double defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}

	@Override
	public final double[] features(final I instance, final CharSequence[] features, final boolean isTraining) {
		final double[] array = new double[Check.size(features, 1, 5000).length];
		final Map<String, Double> feats = features(instance, isTraining).asMap();
		for (int i = 0; i < features.length; ++i) {
			final Double feat = feats.get(features[i]);
			array[i] = feat == null ? defaultValue : feat.doubleValue();
		}
		return array;
	}

	@Override
	public String getReport() {
		throw new IllegalAccessError();
	}

	@Override
	public Object[] getComponents() {
		return new Object[] { new Parameter("default_value", defaultValue) };
	}
}
