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
package com.insightml.math.distributions;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.utils.Check;
import com.insightml.utils.Pair;
import com.insightml.utils.types.AbstractClass;

import java.util.Random;

public final class DiscreteDistribution<T> extends AbstractClass
		implements IDiscreteDistribution<T>, Iterable<Entry<T, Double>> {

	private static final long serialVersionUID = -8857716547353316720L;

	private LinkedHashMap<T, Double> map;

	public DiscreteDistribution() {
	}

	DiscreteDistribution(final LinkedHashMap<T, Double> map) {
		this.map = map;
	}

	public boolean contains(final T key) {
		return map.containsKey(key);
	}

	@Override
	public double get(final T key) {
		return Check.notNull(map.get(key), key).doubleValue();
	}

	public double get(final T key, final double defaultValue) {
		final Double value = map.get(key);
		return value == null ? defaultValue : value.doubleValue();
	}

	@Override
	public Pair<T, Double> getMax() {
		double max = 0;
		T key = null;
		for (final Entry<T, Double> entry : map.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				key = entry.getKey();
			}
		}
		return new Pair<>(key, max);
	}

	@Override
	public T sample(final Random random) {
		if (map.size() == 1) {
			return map.keySet().iterator().next();
		}
		final double rand = random.nextDouble();
		double sum = 0;
		for (final Entry<T, Double> entry : map.entrySet()) {
			sum += entry.getValue();
			if (rand <= sum) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException();
	}

	public SumMap<T> filter(final double min) {
		final SumMapBuilder<T> builder = new SumMapBuilder<>(true, false);
		for (final Entry<T, Double> entry : map.entrySet()) {
			if (entry.getValue() >= min) {
				builder.put(entry.getKey(), entry.getValue());
			}
		}
		return builder.build(0);
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Map<T, Double> getMap() {
		return map;
	}

	@Override
	public Iterator<Entry<T, Double>> iterator() {
		return map.entrySet().iterator();
	}

	public static int sample(final double[] discreteDistribution, final Random random) {
		final double rand = random.nextDouble();
		double sum = 0;
		for (int i = 0; i < discreteDistribution.length; ++i) {
			sum += discreteDistribution[i];
			if (rand <= sum) {
				return i;
			}
		}
		throw new IllegalStateException(rand + " / " + sum);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public static final class DistributionBuilder<T> {

		private final Map<T, Double> map = new LinkedHashMap<>();

		public void put(final T key, final double value) {
			Check.state(value >= -0.0001 && value <= 1.0001 && map.put(Check.notNull(key), value) == null);
		}

		public DiscreteDistribution<T> build() {
			validate();
			return new DiscreteDistribution<>(new LinkedHashMap<>(map));
		}

		private void validate() {
			if (map.isEmpty()) {
				return;
			}
			double sum = 0;
			for (final Double value : map.values()) {
				sum += value.doubleValue();
			}
			Check.num(sum, 0.9999999, 1.0000001);
		}
	}

}
