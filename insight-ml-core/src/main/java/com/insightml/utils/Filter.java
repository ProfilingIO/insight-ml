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
package com.insightml.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class Filter {

	private Filter() {
	}

	public static <T> List<T> filter(final Iterable<T> iterable, final T[] filter) {
		final List<T> list = new LinkedList<>();
		for (final T it : iterable) {
			if (!Arrays.contains(filter, it)) {
				list.add(it);
			}
		}
		return list;
	}

	public static <T, N extends Number> Map<T, N> byMin(final Map<T, N> map, final double min) {
		Check.num(min, 0, 50000);
		final Map<T, N> filtered = new LinkedHashMap<>(map.size());
		for (final Entry<T, N> entry : map.entrySet()) {
			if (entry.getValue().doubleValue() >= min) {
				filtered.put(entry.getKey(), entry.getValue());
			}
		}
		return filtered;
	}

	public static <K, V> Map<K, V> byValue(final Map<K, V> map, final Predicate<V> filter) {
		final Map<K, V> result = new LinkedHashMap<>(map.size());
		for (final Entry<K, V> entry : map.entrySet()) {
			if (filter.test(entry.getValue())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
}
