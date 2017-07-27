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
package com.insightml.math.vectors;

import java.util.Map;
import java.util.Map.Entry;

import com.insightml.utils.Check;
import com.insightml.utils.Maps;

public final class VectorSimilarity {
	private VectorSimilarity() {
	}

	public static <K> double similarity(final Map<? extends K, ? extends Number> map1,
			final Map<? extends K, ? extends Number> map2, final IVectorSimilarity<K> base) {
		final int size1 = Check.num(map1.size(), 1, 9999999);
		final int size2 = Check.num(map2.size(), 1, 9999999);

		final Map<K, Number> features = Maps.create(Math.max(size1, size2) * 2);
		features.putAll(map1);
		int zeros = 0;
		for (final K string : map2.keySet()) {
			if (!features.containsKey(string)) {
				features.put(string, 0.0);
				++zeros;
			}
		}
		// no common elements
		if (zeros == size2) {
			return 0;
		}

		final double[] vector1 = new double[features.size()];
		final double[] vector2 = new double[vector1.length];
		int index = -1;
		for (final Entry<K, Number> feature : features.entrySet()) {
			vector1[++index] = feature.getValue().doubleValue();
			@SuppressWarnings("unlikely-arg-type")
			final Number v2 = map2.get(feature.getKey());
			vector2[index] = v2 == null ? 0 : v2.doubleValue();
		}

		final double sim = base.similarity(vector1, vector2);
		return Check.num(sim, 0.00000000, 1.000001);
	}

}
