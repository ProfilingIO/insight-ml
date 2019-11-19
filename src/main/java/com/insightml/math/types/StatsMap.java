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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.insightml.math.statistics.IStats;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.cache.Cache;

public final class StatsMap<T> extends AbstractClass implements Iterable<Entry<T, Stats>>, Serializable {

	private static final long serialVersionUID = 6753731632670905853L;

	private final StatsCache<T> cache = new StatsCache<>();

	public Stats add(final T key, final double value) {
		final Stats stats = cache.getOrLoad(key);
		stats.add(value);
		return stats;
	}

	public Stats add(final T key, final Stats stats) {
		final Stats local = cache.getOrLoad(key);
		local.add(stats);
		return local;
	}

	public void addAll(final Set<Entry<T, Double>> entries) {
		for (final Entry<T, Double> entry : entries) {
			cache.getOrLoad(entry.getKey()).add(entry.getValue().doubleValue());
		}
	}

	public void addAll(final Iterable<? extends Entry<T, ? extends IStats>> entries) {
		for (final Entry<T, ? extends IStats> entry : entries) {
			cache.getOrLoad(entry.getKey()).add(entry.getValue());
		}
	}

	public IStats get(final T key) {
		return cache.get(key);
	}

	public double getSum(final T key, final double defaultValue) {
		final IStats stats = get(key);
		return stats == null ? defaultValue : stats.getSum();
	}

	public Map<T, IStats> filter(final int minN) {
		final Map<T, IStats> map = new HashMap<>();
		for (final Entry<T, Stats> entry : cache) {
			if (entry.getValue().getN() >= minN) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	public double sumAll() {
		double sum = 0;
		for (final Entry<T, Stats> val : cache) {
			sum += val.getValue().getSum();
		}
		return sum;
	}

	public int countAll() {
		int sum = 0;
		for (final Entry<T, Stats> val : cache) {
			sum += val.getValue().getN();
		}
		return sum;
	}

	public int size() {
		return cache.size();
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public Map<T, ? extends IStats> asMap() {
		return cache.asMap();
	}

	public StatsMap<T> copy() {
		final StatsMap<T> copy = new StatsMap<>();
		for (final Entry<T, Stats> entry : this) {
			copy.cache.put(entry.getKey(), entry.getValue().copy());
		}
		return copy;
	}

	@Override
	public Iterator<Entry<T, Stats>> iterator() {
		return cache.iterator();
	}

	@Override
	public String toString() {
		return cache.asMap().toString();
	}

	public static final class StatsCache<T> extends Cache<T, Stats> {
		private static final long serialVersionUID = 2454716585003160754L;

		StatsCache() {
			super(1024);
		}

		@Override
		protected Stats load(final T key) {
			return new Stats();
		}
	}

}
