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

import java.util.Calendar;
import java.util.Collection;

public final class Check {
	private Check() {
	}

	public static void isNull(final Object object) {
		isNull(object, null);
	}

	public static void isNull(final Object object, final Object output) {
		Check.state(object == null, output == null ? object : output);
	}

	public static <T> T notNull(final T val) {
		if (val == null) {
			throw new NullPointerException();
		}
		return val;
	}

	public static <T> T notNull(final T val, final Object output) {
		if (val == null) {
			throw new NullPointerException(output.toString());
		}
		return val;
	}

	public static String notEmpty(final String val, final Object output) {
		state(!val.isEmpty());
		return val;
	}

	public static int num(final int number, final int min, final int max) {
		return num(number, min, max, null);
	}

	public static int num(final int num, final int min, final int max, final Object output) {
		if (num < min || num > max) {
			thrw(num + " is not within [" + min + "," + max + "]", output);
		}
		return num;
	}

	public static long num(final long num, final long min, final long max) {
		num(num, min, max, null);
		return num;
	}

	public static float num(final float num, final double min, final double max) {
		num(num, min, max, null);
		return num;
	}

	public static float num(final float number, final double min, final double max, final Object output) {
		if (number < min || number > max || Double.isNaN(number)) {
			thrw(number + " is not within [" + min + "," + max + "]", output);
		}
		return number;
	}

	public static double num(final double number, final double min, final double max) {
		return num(number, min, max, null);
	}

	public static double num(final double number, final double min, final double max, final Object output) {
		if (number < min || number > max || Double.isNaN(number)) {
			thrw(number + " is not within [" + min + "," + max + "]", output);
		}
		return number;
	}

	public static String length(final String str, final int min, final int max) {
		return length(str, min, max, null);
	}

	public static String length(final String str, final int min, final int max, final Object debug) {
		final int length = Check.notNull(str, debug).length();
		if (length < min) {
			thrw("'" + str + "' is too short (" + length + ")", debug);
		}
		if (length > max) {
			thrw((length > 1000 ? "text" : "'" + str + "'") + " is too long (" + length + ")", debug);
		}
		return str;
	}

	public static double[] size(final double[] array, final int min, final int max) {
		return size(array, min, max, null);
	}

	public static double[] size(final double[] array, final int min, final int max, final Object debug) {
		if (array.length < min || array.length > max) {
			thrw(min + " <= x <= " + max + " expected, got " + array.length, debug);
		}
		return array;
	}

	public static <T> T[] size(final T[] array, final int min, final int max) {
		if (array.length < min || array.length > max) {
			thrw(min + " <= x <= " + max + " expected, got " + array.length, java.util.Arrays.toString(array));
		}
		return array;
	}

	public static <T extends Collection<?>> T size(final T collection, final int min, final int max) {
		return size(collection, min, max, null);
	}

	public static <T extends Collection<?>> T size(final T collection, final int min, final int max,
			final Object debug) {
		final int size = collection.size();
		if (size < min || size > max) {
			thrw(min + " <= x <= " + max + " expected, got " + collection.size()
					+ (size <= 20 ? ": " + collection : ""), debug);
		}
		return collection;
	}

	public static <T> void equals(final T obj1, final T obj2) {
		equals(obj1, obj2, null);
	}

	public static <T> void equals(final T obj1, final T obj2, final Object origin) {
		if (obj1 == null && obj2 != null || obj1 != null && !obj1.equals(obj2)) {
			thrw(obj1 + " and " + obj2 + " are not equal", origin);
		}
	}

	public static boolean state(final boolean expression) {
		if (!expression) {
			throw new IllegalStateException("illegal state observed");
		}
		return expression;
	}

	public static void state(final boolean expression, final Object output) {
		if (!expression) {
			throw new IllegalStateException(output + "");
		}
	}

	public static void argument(final boolean expression) {
		if (!expression) {
			thrw("expression failed", null);
		}
	}

	public static void argument(final boolean expression, final Object output) {
		if (!expression) {
			thrw(output.toString(), null);
		}
	}

	public static void notAfter(final Calendar a, final Calendar b) {
		notAfter(a, b, null);
	}

	private static void notAfter(final Calendar a, final Calendar b, final Object origin) {
		if (a.after(b)) {
			thrw(a.getTime() + " is after " + b.getTime(), origin);
		}
	}

	private static void thrw(final String msg, final Object origin) {
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		int i = 0;
		for (final StackTraceElement el : stack) {
			if (!el.getClassName().endsWith("Thread") && !el.getClassName().endsWith("Check")) {
				break;
			}
			++i;
		}
		throw new IllegalArgumentException(fill(msg + (origin == null ? "" : " for " + origin), 50) + " ("
				+ (stack.length > i + 1 ? toString(stack[i + 1]) + " : " : "") + toString(stack[i]) + ")");
	}

	private static String toString(final StackTraceElement el) {
		return el.getFileName() + ":" + el.getLineNumber();
	}

	// TODO: Borrowed from UiUtils. Reuse if possible!
	private static String fill(final Object object, final int length) {
		final StringBuilder builder = new StringBuilder(length);
		final String string = object.toString();
		builder.append(string);
		for (int j = string.length(); j < length; ++j) {
			builder.append(' ');
		}
		return builder.toString();
	}

}
