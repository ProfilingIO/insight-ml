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

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Arguments implements IArguments {
	private static final long serialVersionUID = 4806144248416561294L;

	private final Map<String, Serializable> values = Maps.create(16);

	Arguments() {
	}

	public Arguments(final String... args) {
		Check.state(args.length % 2 == 0);
		for (int i = 0; i < args.length; i += 2) {
			set(args[i], args[i + 1]);
		}
	}

	public Arguments(final Serializable... args) {
		Check.state(args.length % 2 == 0);
		for (int i = 0; i < args.length; i += 2) {
			set((String) args[i], args[i + 1]);
		}
	}

	@Override
	public Set<Entry<String, Serializable>> entrySet() {
		return values.entrySet();
	}

	@Override
	public boolean containsKey(final String key) {
		return values.containsKey(key);
	}

	@Override
	public <T extends Serializable> T get(final String key) {
		Check.state(containsKey(key), key + " is not set.");
		return (T) values.get(key);
	}

	public void set(final String key, final Serializable value) {
		final Serializable old = values.put(key, value);
		if (old != null && !value.equals(old)) {
			throw new IllegalAccessError("Overwrote " + key + " from " + old + " to " + value);
		}
	}

	@Override
	public int toInt(final String key) {
		final Serializable value = values.get(key);
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value instanceof String) {
			return Integer.parseInt((String) value);
		}
		throw new IllegalArgumentException(key + " is not set or neither Number nor String.");
	}

	@Override
	public Integer toInt(final String key, final Integer def) {
		try {
			return toInt(key);
		} catch (final IllegalArgumentException e) {
			return def;
		}
	}

	@Override
	public double toDouble(final String key) {
		final Serializable value = values.get(key);
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		if (value instanceof String) {
			return Double.parseDouble((String) value);
		}
		throw new IllegalArgumentException(key + " is not set or neither Number nor String.");
	}

	@Override
	public Double toDouble(final String key, final Double def) {
		try {
			return toDouble(key);
		} catch (final IllegalArgumentException e) {
			return def;
		}
	}

	@Override
	public boolean bool(final String key) {
		final Serializable value = values.get(key);
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		if (value instanceof String) {
			return Boolean.parseBoolean((String) value);
		}
		throw new IllegalArgumentException(key + " is not set or neither Boolean nor String.");
	}

	@Override
	public boolean bool(final String key, final boolean def) {
		try {
			return bool(key);
		} catch (final IllegalArgumentException e) {
			return def;
		}
	}

	@Override
	public String toString(final String key) {
		return get(key);
	}

	@Override
	public String toString(final String key, final String def) {
		final String val = (String) values.get(key);
		return val == null ? def : val;
	}

	public Arguments copy() {
		final Arguments copy = new Arguments();
		copy.values.putAll(values);
		return copy;
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		return values.equals(((Arguments) obj).values);
	}

	@Override
	public String toString() {
		return values.toString();
	}

}
