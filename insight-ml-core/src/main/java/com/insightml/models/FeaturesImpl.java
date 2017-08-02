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
package com.insightml.models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.insightml.data.features.FeaturesConsumer;
import com.insightml.utils.Check;
import com.insightml.utils.Maps;
import com.insightml.utils.types.DoublePair;

public final class FeaturesImpl implements Features, FeaturesConsumer {

	private final List<DoublePair<String>> features = new LinkedList<>();

	@Override
	public void add(final String feature, final double value) {
		features.add(new DoublePair<>(feature, value));
	}

	@Override
	public String[] featureNames() {
		final String[] names = new String[features.size()];
		int i = -1;
		for (final DoublePair<String> entry : this) {
			names[++i] = entry.getKey();
		}
		return names;
	}

	@Override
	public double[] toDoubleArray() {
		final double[] array = new double[features.size()];
		int i = -1;
		for (final DoublePair<String> entry : this) {
			array[++i] = entry.getValue();
		}
		return array;
	}

	@Override
	public Iterator<DoublePair<String>> iterator() {
		return features.iterator();
	}

	@Override
	public Map<String, Double> asMap() {
		final Map<String, Double> map = Maps.create(features.size());
		for (final DoublePair<String> entry : this) {
			Check.isNull(map.put(entry.getKey(), entry.getValue()), entry.getKey());
		}
		return map;
	}

	@Override
	public FeaturesImpl copy() {
		final FeaturesImpl copy = new FeaturesImpl();
		copy.features.addAll(features);
		return copy;
	}

}
