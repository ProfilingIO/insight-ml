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
import java.util.Map;

import javax.annotation.Nonnull;

public final class Maps {
	private Maps() {
	}

	@Nonnull
	public static <K, V> LinkedHashMap<K, V> create(final int expected) {
		return new LinkedHashMap<>(Sets.capacity(expected));
	}

	public static <K, V> V firstValueAvailable(final Map<K, V> map, final K... keys) {
		for (final K key : keys) {
			final V value = map.get(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

}
