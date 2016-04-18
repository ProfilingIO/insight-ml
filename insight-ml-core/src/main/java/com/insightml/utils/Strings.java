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

public final class Strings {

	private Strings() {
	}

	public static int count(final String text, final char cha) {
		int count = 0;
		for (final char ch : text.toCharArray()) {
			if (cha == ch) {
				++count;
			}
		}
		return count;
	}

	public static int countNum(final String text) {
		int count = 0;
		for (final char ch : text.toCharArray()) {
			final int num = ch;
			if (num <= 57 && num >= 48) {
				++count;
			}
		}
		return count;
	}

	public static char[] replace(final CharSequence string, final char[] search, final char[] replace) {
		final char[] copy = new char[string.length()];
		for (int i = 0; i < copy.length; ++i) {
			copy[i] = string.charAt(i);
			for (int j = 0; j < search.length; ++j) {
				if (copy[i] == search[j]) {
					copy[i] = replace[j];
					break;
				}
			}
		}
		return copy;
	}

	public static boolean contains(final String string, final char... chrs) {
		for (int i = 0; i < string.length(); ++i) {
			for (final char chr : chrs) {
				if (string.charAt(i) == chr) {
					return true;
				}
			}
		}
		return false;
	}

	public static String repeat(final String string, final int times) {
		final StringBuilder builder = new StringBuilder(string.length() * times);
		for (int i = 0; i < times; ++i) {
			builder.append(string);
		}
		return builder.toString();
	}

	public static String removeStart(final String string, final char chr) {
		return string.charAt(0) == chr ? string.substring(1) : string;
	}

	public static String removeEnd(final String string, final char chr) {
		if (string.isEmpty()) {
			return string;
		}
		final int length = string.length();
		return string.charAt(length - 1) == chr ? string.substring(0, length - 1) : string;
	}

	public static String substringAfterLast(final String string, final char chr) {
		return string.substring(string.lastIndexOf(chr) + 1);
	}

	public static String join(final String[] str, final int start, final int end) {
		Check.num(start, 0, end - 1);
		final StringBuilder builder = new StringBuilder();
		for (int i = start; i < end; ++i) {
			builder.append(' ');
			builder.append(str[i]);
		}
		return builder.substring(1);
	}

	public static String limit(final String txt, final int limit) {
		return txt.length() > limit ? txt.substring(0, limit) + " ..." : txt;
	}

	public static String capitalize(final String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static String trim(final String string, final int fromStart, final int fromEnd) {
		final String trimmed = string.trim();
		return trimmed.substring(fromStart, trimmed.length() - fromEnd);
	}
}
