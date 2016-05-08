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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.math3.util.Pair;

public final class Collections {

	public enum SortOrder {
		ASCENDING, DESCENDING
	}

	private Collections() {
	}

	public static <I> ConcurrentLinkedQueue<I> newConcurrentList() {
		return new ConcurrentLinkedQueue<>();
	}

	public static <I> Queue<I> newConcurrentList(final Iterable<I> elements) {
		final Queue<I> queue = newConcurrentList();
		for (final I element : elements) {
			queue.add(element);
		}
		return queue;
	}

	public static <I> Set<I> matches(final Collection<I> col1, final Collection<I> col2) {
		final Set<I> builder = new HashSet<>();
		for (final I item : col1) {
			if (col2.contains(item)) {
				builder.add(item);
			}
		}
		return builder;
	}

	public static <T, N extends Number> LinkedList<Pair<T, N>> getMax(final Iterable<Pair<T, N>> map) {
		LinkedList<Pair<T, N>> maxEntry = new LinkedList<>();
		for (final Pair<T, N> entry : map) {
			if (maxEntry.size() == 0
					|| entry.getSecond().doubleValue() > maxEntry.getFirst().getSecond().doubleValue()) {
				maxEntry = new LinkedList<>();
				maxEntry.add(entry);
			} else if (entry.getSecond().doubleValue() == maxEntry.getFirst().getSecond().doubleValue()) {
				maxEntry.add(entry);
			}
		}
		return maxEntry;
	}

	public static <T> Set<T> sort(final Set<T> items) {
		final Set<T> set = new TreeSet<>();
		set.addAll(items);
		return set;
	}

	public static <T extends Comparable<? super T>> List<T> sortAscending(final Iterable<T> items) {
		final List<T> list = new LinkedList<>();
		for (final T item : items) {
			list.add(item);
		}
		java.util.Collections.sort(list, (arg0, arg1) -> {
			final int comp = arg0.compareTo(arg1);
			if (comp == 0) {
				throw new IllegalStateException(arg0 + ", " + arg1);
			}
			return comp;
		});
		return list;
	}

	public static <T extends Comparable<? super T>> List<T> sortDescending(final Iterable<T> items) {
		final List<T> list = new LinkedList<>();
		for (final T item : items) {
			list.add(item);
		}
		java.util.Collections.sort(list, (arg0, arg1) -> {
			final int comp = arg1.compareTo(arg0);
			if (comp == 0) {
				throw new IllegalStateException(arg0 + ", " + arg1);
			}
			return comp;
		});
		return list;
	}

	public static <T, N extends Number> LinkedList<Pair<T, N>> sortDescending2(final List<Pair<T, N>> list) {
		final LinkedList<Pair<T, N>> newList = new LinkedList(list);
		java.util.Collections.sort(newList, (o1, o2) -> {
			int comp = Double.valueOf(o1.getSecond().doubleValue()).compareTo(o2.getSecond().doubleValue());
			if (comp == 0) {
				if (o1.getFirst() instanceof Comparable) {
					comp = ((Comparable) o1.getFirst()).compareTo(o2.getFirst());
					Check.state(comp != 0);
				} else {
					return 0;
				}
			}
			return comp > 0 ? -1 : 1;
		});
		return newList;
	}

	public static <T, N extends Number> List<Pair<T, N>> sortDescendingIncomp(final List<Pair<T, N>> list) {
		final List<Pair<T, N>> newList = new LinkedList<>(list);
		java.util.Collections.sort(newList, (o1, o2) -> {
			final int comp = Double.valueOf(o1.getSecond().doubleValue()).compareTo(o2.getSecond().doubleValue());
			if (comp == 0) {
				// throw new IllegalStateException();
				return 0;
			}
			return comp > 0 ? -1 : 1;
		});
		return newList;
	}

	public static <T, N extends Number> Map<T, N> sort(final Map<T, N> map, final SortOrder order) {
		final TreeMap<T, N> sorted = new TreeMap<>((o1, o2) -> {
			final double o1Value = map.get(o1).doubleValue();
			final double o2Value = map.get(o2).doubleValue();
			if (o1Value == o2Value) {
				if (o1 instanceof Comparable) {
					return ((Comparable<T>) o1).compareTo(o2);
				}
				// TODO: This can be very unexpected!!!
				return Integer.compare(o1.hashCode(), o2.hashCode());
			}
			if (order == SortOrder.DESCENDING) {
				return o1Value < o2Value ? 1 : -1;
			}
			return o1Value > o2Value ? 1 : -1;
		});
		sorted.putAll(map);
		return sorted;
	}

	public static <T, N extends Comparable<N>> Map<T, N> sortAsc(final Map<T, N> map) {
		final TreeMap<T, N> sorted = new TreeMap<>((o1, o2) -> {
			if (o1 == o2) {
				return 0;
			}
			final N v1 = map.get(o1);
			final N v2 = map.get(o2);
			final int comp = v1.compareTo(v2);
			Check.state(comp != 0);
			return comp;
		});
		sorted.putAll(map);
		return sorted;
	}

	public static <K, V> Map<K, V> subMap(final Map<K, V> sorted, final int n) {
		if (Check.num(n, 1, 9999999) >= sorted.size()) {
			return sorted;
		}
		final Map<K, V> map = new LinkedHashMap<>();
		int i = 0;
		for (final Entry<K, V> entry : sorted.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
			if (++i >= n) {
				break;
			}
		}
		return map;
	}

	public static <T, N extends Number> Map<T, N> getTopN(final Map<T, N> map, final int n, final double min) {
		return subMap(sort(Filter.byMin(map, min), SortOrder.DESCENDING), n);
	}

	public static <T, N extends Number> Map<T, N> descending(final Map<T, N> map, final double min) {
		return sort(Filter.byMin(map, min), SortOrder.DESCENDING);
	}

	public static <T> List<T>[] split(final List<T> list, final int parts) {
		final List<T>[] prts = new List[parts];
		for (int i = 0; i < prts.length; ++i) {
			prts[i] = new LinkedList<>();
		}
		int i = -1;
		for (final T el : list) {
			prts[++i % parts].add(el);
		}
		return prts;
	}

	public static <T> LinkedList<T> merge(final Iterable<? extends Iterable<T>> run) {
		final LinkedList<T> list = new LinkedList<>();
		for (final Iterable<T> l : run) {
			for (final T e : l) {
				list.add(e);
			}
		}
		return list;
	}

	public static <T> List<T> subList(final List<T> sentences, final int max) {
		if (sentences.size() < max) {
			return sentences;
		}
		return sentences.subList(0, max);
	}

	public static <K> Collection<K> keys(final List<? extends Pair<K, ?>> subList) {
		final List<K> keys = new LinkedList<>();
		for (final Pair<K, ?> entry : subList) {
			keys.add(entry.getKey());
		}
		return keys;
	}

	public static <V> LinkedList<V> values(final List<? extends Pair<?, V>> run) {
		final LinkedList<V> values = new LinkedList<>();
		for (final Pair<?, V> entry : run) {
			if (entry.getValue() != null) {
				values.add(entry.getValue());
			}
		}
		return values;
	}

}
