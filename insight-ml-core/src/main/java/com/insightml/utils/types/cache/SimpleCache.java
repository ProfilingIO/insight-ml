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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import com.insightml.utils.types.ICopyable;

public final class SimpleCache<K, V> extends Cache<K, V> {

	private static final long serialVersionUID = -3757108358704975812L;

	private Class<V> clazz;
	private boolean passKey;

	SimpleCache() {
	}

	public SimpleCache(final Class<V> clazz, final int expectedSize, final boolean passKey) {
		super(expectedSize);
		this.clazz = clazz;
		this.passKey = passKey;
	}

	@Override
	protected V load(final K key) {
		try {
			if (passKey) {
				final Constructor<?>[] cons = clazz.getConstructors();
				if (cons.length == 0) {
					throw new IllegalStateException(clazz.toString());
				}
				return (V) cons[0].newInstance(key);
			}
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	public Class<V> getValueClass() {
		return clazz;
	}

	public SimpleCache<K, V> copy() {
		final SimpleCache<K, V> copy = new SimpleCache<>(clazz, size(), passKey);
		for (final Entry<K, V> entry : this) {
			copy.put(entry.getKey(), (V) ((ICopyable) entry.getValue()).copy());
		}
		return copy;
	}
}