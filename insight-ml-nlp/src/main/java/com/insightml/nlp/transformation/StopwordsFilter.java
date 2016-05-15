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
package com.insightml.nlp.transformation;

import java.util.Locale;

import com.insightml.nlp.ISentence.Tag;
import com.insightml.utils.Check;
import com.insightml.utils.Strings;

public final class StopwordsFilter extends WordFilter {

	private static final long serialVersionUID = -2582894599101095196L;

	private int minLength;
	private int maxLength;

	StopwordsFilter() {
	}

	public StopwordsFilter(final int minLength, final String... wordFiles) {
		this(minLength, 999, wordFiles);
	}

	public StopwordsFilter(final int minLength, final int maxLength, final String[] wordFiles) {
		this(minLength, maxLength, false, wordFiles);
	}

	public StopwordsFilter(final int minLength, final int maxLength, final boolean postFilter,
			final String[] wordFiles) {
		super(Tag.STOPWORDS, postFilter, wordFiles);
		this.minLength = Check.num(minLength, 1, 10);
		this.maxLength = Check.num(maxLength, minLength, 999);
	}

	@Override
	public String getDescription() {
		return "Filtering based on manually defined stopwords lists.";
	}

	@Override
	public boolean contains(final String word) {
		return word.length() < minLength || word.length() > maxLength || super.contains(word) || Strings.contains(word,
				'=', ';', '(', ')', '[', '\\', ':', '"', '>', '�', 'é', 'î', 'ô', 'в', 'и', '→', '≥', '∈');
	}

	public String removeStopwords(final String text) {
		final int length = text.length();
		final StringBuilder result = new StringBuilder(length);
		final StringBuilder token = new StringBuilder();
		for (int i = 0; i <= length; ++i) {
			final char chr = i < length ? text.charAt(i) : 0;
			if (Character.isLetter(chr) || chr == '\'' || chr == '@') {
				token.append(chr);
			} else if (token.length() > 0) {
				final String tk = token.toString().toLowerCase(Locale.ENGLISH);
				if (!contains(tk)) {
					result.append(tk);
					result.append(' ');
				}
				token.setLength(0);
			}
		}
		return result.toString().replaceAll("[ ]+", " ").replaceFirst("^[\\s,']+", "").trim();
	}

}
