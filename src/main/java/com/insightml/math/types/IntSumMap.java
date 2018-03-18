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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

public final class IntSumMap<T> extends AbstractSumMap<T, Integer> {

	private static final long serialVersionUID = -7523006897191469847L;

	IntSumMap() {
	}

	IntSumMap(final LinkedHashMap<T, Integer> map, final int min) {
		super(map, min);
	}

	@Override
	public IntSumMap<T> filter(final double min, final double max) {
		final LinkedHashMap<T, Integer> map = new LinkedHashMap<>();
		for (final Entry<T, Integer> entry : this) {
			if (entry.getValue() >= min && entry.getValue() <= max) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return new IntSumMap<>(map, getMin());
	}

	public static <T> IntSumMapBuilder<T> builder(final boolean concurrent, final int expectedSize) {
		return new IntSumMapBuilder<>(concurrent, expectedSize, false);
	}

	public static final class IntSumMapBuilder<T> extends AbstractClass implements Iterable<Entry<T, AtomicInteger>> {

		private Map<T, AtomicInteger> map;
		private boolean isPut;

		IntSumMapBuilder() {
		}

		public IntSumMapBuilder(final boolean concurrent, final int expectedSize, final boolean isPut) {
			map = concurrent ? new ConcurrentHashMap<>(expectedSize) : new LinkedHashMap<>(expectedSize);
			this.isPut = isPut;
		}

		public void put(final T key, final int count) {
			put(key, count, false);
		}

		public void put(final T key, final int count, final boolean allowOverwrite) {
			Check.state(isPut && (map.put(key, new AtomicInteger(count)) == null || allowOverwrite), key);
		}

		public int increment(final T key, final int count) {
			Check.state(!isPut && count != 0);
			if (map instanceof ConcurrentMap) {
				if (((ConcurrentMap<T, AtomicInteger>) map).putIfAbsent(key, new AtomicInteger(count)) != null) {
					return map.get(key).addAndGet(count);
				}
			} else {
				final AtomicInteger old = map.get(key);
				if (old == null) {
					map.put(key, new AtomicInteger(count));
				} else {
					return old.addAndGet(count);
				}
			}
			return count;
		}

		public int get(final T key) {
			final AtomicInteger val = map.get(key);
			return val == null ? 0 : val.get();
		}

		public boolean contains(final T key) {
			return map.containsKey(key);
		}

		public int size() {
			return map.size();
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public IntSumMap<T> build(final int min) {
			Check.num(min, 0, 15000);
			final LinkedHashMap<T, Integer> builder = new LinkedHashMap<>(map.size());
			for (final Entry<T, AtomicInteger> entry : map.entrySet()) {
				final int value = entry.getValue().get();
				if (value >= min) {
					builder.put(entry.getKey(), value);
				}
			}
			return new IntSumMap<>(builder, min);
		}

		public void addAll(final IntSumMapBuilder<T> counter) {
			for (final Entry<T, AtomicInteger> entry : counter) {
				increment(entry.getKey(), entry.getValue().get());
			}
		}

		public IntSumMapBuilder<T> copy() {
			final IntSumMapBuilder<T> copy = new IntSumMapBuilder<>();
			copy.map = map instanceof ConcurrentHashMap ? new ConcurrentHashMap<>(map.size()) : new LinkedHashMap<>(
					map.size());
			for (final Entry<T, AtomicInteger> entry : map.entrySet()) {
				copy.map.put(entry.getKey(), new AtomicInteger(entry.getValue().get()));
			}
			copy.isPut = isPut;
			return copy;
		}

		@Override
		public Iterator<Entry<T, AtomicInteger>> iterator() {
			return map.entrySet().iterator();
		}
	}

}
