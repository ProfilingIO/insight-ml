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

import java.util.Random;

public final class Utils {

	private static int seed;

	private Utils() {
	}

	public static double toDouble(final Object obj) {
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj ? 1.0 : 0.0;
		}
		if (obj instanceof String) {
			if ("true".equals(obj)) {
				return 1;
			}
			if ("false".equals(obj)) {
				return 0;
			}
			return Double.valueOf((String) obj);
		}
		throw new IllegalArgumentException(obj + "");
	}

	public static float toFloat(final Object obj) {
		if (obj instanceof Number) {
			return ((Number) obj).floatValue();
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj ? 1.0f : 0.0f;
		}
		if (obj instanceof String) {
			if ("true".equals(obj)) {
				return 1;
			}
			if ("false".equals(obj)) {
				return 0;
			}
			return Float.valueOf((String) obj);
		}
		throw new IllegalArgumentException(obj + "");
	}

	public static double[] toDouble(final Object[] obj) {
		final double[] res = new double[obj.length];
		for (int i = 0; i < obj.length; ++i) {
			res[i] = toDouble(obj[i]);
		}
		return res;
	}

	public static Random random() {
		return new Random(seed);
	}

	public static int getRandomSeed() {
		return seed;
	}

	public static void setRandomSeed(final int seeed) {
		Utils.seed = seeed;
	}

}
