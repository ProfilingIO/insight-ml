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
package com.insightml.math.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.AtomicDouble;
import com.insightml.utils.Check;

public final class SumMap<T> extends AbstractSumMap<T, Double> {

	private static final long serialVersionUID = -7523006897191469847L;

	SumMap() {
	}

	SumMap(final LinkedHashMap<T, Double> map, final double min) {
		super(map, min);
	}

	public static <T> SumMapBuilder<T> builder(final boolean synched) {
		return new SumMapBuilder<>(false, synched);
	}

	public double sumAll() {
		double sum = 0;
		for (final Double entry : getMap().values()) {
			sum += entry;
		}
		return sum;
	}

	@Override
	public ISumMap<T, Double> filter(final double min, final double max) {
		final LinkedHashMap<T, Double> map = new LinkedHashMap<>();
		for (final Entry<T, Double> entry : this) {
			if (entry.getValue() >= min && entry.getValue() <= max) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return new SumMap<>(map, getMin());
	}

	public static final class SumMapBuilder<T> {

		private final Map<T, AtomicDouble> map;
		private final boolean isPut;

		public SumMapBuilder(final boolean isPut, final boolean synched) {
			this.isPut = isPut;
			this.map = synched ? new ConcurrentHashMap<>() : new LinkedHashMap<>();
		}

		public boolean contains(final T key) {
			return map.containsKey(key);
		}

		public Double get(final T key) {
			return map.get(key).get();
		}

		public void put(final T key, final double count) {
			Check.state(isPut && count != 0);
			Check.state(map.put(key, new AtomicDouble(count)) == null, key);
		}

		public void increment(final T key, final double count) {
			Check.state(!isPut);
			map.computeIfAbsent(key, k -> new AtomicDouble(0)).addAndGet(count);
		}

		public void incAll(final Set<Entry<T, Double>> entrySet) {
			for (final Entry<T, Double> entry : entrySet) {
				increment(entry.getKey(), entry.getValue());
			}
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public SumMap<T> build(final double min) {
			final LinkedHashMap<T, Double> builder = new LinkedHashMap<>(map.size());
			for (final Entry<T, AtomicDouble> entry : map.entrySet()) {
				final double value = entry.getValue().get();
				if (value >= min) {
					builder.put(entry.getKey(), value);
				}
			}
			return new SumMap<>(builder, min);
		}

	}
}
