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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.insightml.math.distributions.DiscreteDistribution;
import com.insightml.math.distributions.DiscreteDistribution.DistributionBuilder;
import com.insightml.math.statistics.IStats;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

abstract class AbstractSumMap<T, N extends Number> extends AbstractClass implements ISumMap<T, N> {

	private static final long serialVersionUID = -7523006897191469847L;

	private LinkedHashMap<T, N> map;
	private N min;

	AbstractSumMap() {
	}

	AbstractSumMap(final LinkedHashMap<T, N> map, final N min) {
		this.map = Check.notNull(map);
		this.min = Check.notNull(min);
	}

	protected N getMin() {
		return min;
	}

	public final boolean contains(final T key) {
		return map.containsKey(key);
	}

	@Override
	public final <C extends Number> C get(final T key) {
		return (C) map.get(Check.notNull(key));
	}

	@Override
	public final <C extends Number> C getOrMin(final T key) {
		return (C) get(key, min);
	}

	@Override
	public <C extends Number> C get(final T key, final C defaultValue) {
		final C entry = get(key);
		return entry == null ? defaultValue : entry;
	}

	@Override
	public final IStats statistics() {
		final Stats stats = new Stats();
		for (final Entry<T, N> entry : map.entrySet()) {
			stats.add(entry.getValue().doubleValue());
		}
		return stats;
	}

	@Override
	public final DiscreteDistribution<T> distribution() {
		Check.state(min.doubleValue() == 0);
		Check.state(!map.isEmpty());
		final DistributionBuilder<T> distribution = new DistributionBuilder<>();
		double sum = 0;
		for (final N value : map.values()) {
			sum += value.doubleValue();
		}
		for (final Entry<T, N> entry : map.entrySet()) {
			distribution.put(entry.getKey(), entry.getValue().doubleValue() / sum);
		}
		return distribution.build();
	}

	public final List<Entry<T, N>> getMostFrequent() {
		final List<Entry<T, N>> list = new LinkedList<>();
		if (map.isEmpty()) {
			return list;
		}
		final double max = statistics().getMax();
		for (final Entry<T, N> entry : map.entrySet()) {
			if (entry.getValue().doubleValue() == max) {
				list.add(entry);
			}
		}
		Check.state(!list.isEmpty());
		return list;
	}

	@Override
	public final LinkedHashMap<T, N> getMap() {
		return map;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public final int size() {
		return map.size();
	}

	@Override
	public final Iterator<Entry<T, N>> iterator() {
		return map.entrySet().iterator();
	}

	@Override
	public final int hashCode() {
		return map.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		return ((AbstractSumMap<T, N>) object).map.equals(map);
	}

}
