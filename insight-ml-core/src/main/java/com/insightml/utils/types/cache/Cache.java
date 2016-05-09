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
package com.insightml.utils.types.cache;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.math3.util.Pair;

import com.google.gson.annotations.Expose;
import com.insightml.utils.Check;
import com.insightml.utils.Maps;
import com.insightml.utils.types.AbstractClass;

public abstract class Cache<K, V> extends AbstractClass implements Iterable<Entry<K, V>>, Serializable {
	private static final long serialVersionUID = 525992178263628297L;

	@Expose
	private Map<K, V> cache;

	protected Cache() {
	}

	public Cache(final int expectedSize) {
		this((Map<K, V>) Maps.create(expectedSize));
	}

	public Cache(final Map<K, V> cache) {
		this.cache = Check.notNull(cache);
	}

	public final V get(final K key) {
		return cache.get(Check.notNull(key));
	}

	public final V getOrLoad(final K key) {
		return getOrLoad(key, () -> load(key));
	}

	protected final V getOrLoad(final K key, final Supplier<V> loader) {
		return cache.computeIfAbsent(key, k -> loader.get());
	}

	protected abstract V load(K key);

	public final int size() {
		return cache.size();
	}

	public final boolean isEmpty() {
		return cache.isEmpty();
	}

	public boolean containsKey(final K key) {
		return cache.containsKey(key);
	}

	public final V remove(final K key) {
		return cache.remove(key);
	}

	public final V put(final K key, final V value) {
		return cache.put(key, value);
	}

	public final Set<K> keys() {
		return cache.keySet();
	}

	public final Map<K, V> asMap() {
		return new LinkedHashMap<>(cache);
	}

	public final List<Pair<K, V>> asList() {
		final List<Pair<K, V>> list = new LinkedList<>();
		for (final Entry<K, V> entry : cache.entrySet()) {
			list.add(new Pair<>(entry.getKey(), entry.getValue()));
		}
		return list;
	}

	public final void clear() {
		cache.clear();
	}

	@Override
	public final Iterator<Entry<K, V>> iterator() {
		return cache.entrySet().iterator();
	}

}
