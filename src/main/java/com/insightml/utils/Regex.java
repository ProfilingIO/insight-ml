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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Regex {

	private Regex() {
	}

	public static Pattern pattern(final String pattern) {
		return Pattern.compile(pattern);
	}

	public static Matcher matcher(final String pattern, final String text) {
		return pattern(pattern).matcher(text);
	}

	public static boolean contains(final String text, final Pattern pattern) {
		return pattern.matcher(text).find();
	}

	public static String find(final Pattern pattern, final String text) {
		return Regex.find(pattern, text, 0, 0);
	}

	@Deprecated
	public static String find(final String pattern, final String text, final int offsetl, final int offsetr) {
		return find(Pattern.compile(pattern), text, offsetl, offsetr);
	}

	public static String find(final Pattern pattern, final String text, final int offsetl, final int offsetr) {
		final Matcher match = pattern.matcher(text);
		String result = null;
		if (match.find()) {
			result = text.substring(match.start() + offsetl, match.end() - offsetr);
		}
		return result;
	}

	public static List<String> findAll(final Pattern pattern, final String text) {
		return Regex.findAll(pattern, text, 0, 0);
	}

	@Deprecated
	public static LinkedList<String> findAll(final String pattern, final String text, final int offsetl,
			final int offsetr) {
		return findAll(Pattern.compile(pattern), text, offsetl, offsetr);
	}

	public static LinkedList<String> findAll(final Pattern pattern, final String text, final int offsetl,
			final int offsetr) {
		final Matcher match = pattern.matcher(text);
		final LinkedList<String> result = new LinkedList<>();
		while (match.find()) {
			final String str = text.substring(match.start() + offsetl, match.end() - offsetr);
			result.add(str);
		}
		return result;
	}

}
