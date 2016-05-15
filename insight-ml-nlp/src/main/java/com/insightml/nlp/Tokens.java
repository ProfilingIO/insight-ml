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
package com.insightml.nlp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.collections.ArrayIterator;

public class Tokens extends AbstractClass implements Serializable, Iterable<IToken> {

	private static final long serialVersionUID = -79598901610999489L;

	private final IToken[] tokens;

	public Tokens(final IToken[] tokens) {
		this.tokens = Check.size(tokens, 1, 999999999);
		for (final IToken token : tokens) {
			Check.notNull(token);
		}
	}

	public final IToken get(final int i) {
		return Check.notNull(tokens[i]);
	}

	public static final IntSumMap<IToken> getHistogram(final Iterable<IToken> tokens, final int min) {
		final IntSumMapBuilder<IToken> histogram = IntSumMap.builder(false, 16);
		for (final IToken token : tokens) {
			histogram.increment(token.toLowerCase(), 1);
		}
		return histogram.build(min);
	}

	public final int numTokens() {
		return tokens.length;
	}

	@Override
	public final Iterator<IToken> iterator() {
		return new ArrayIterator<>(tokens);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(tokens);
	}

	@Override
	public boolean equals(final Object object) {
		return this == object || Arrays.deepEquals(((Tokens) object).tokens, tokens);
	}

	public final String asString(final String separator) {
		final StringBuilder builder = new StringBuilder(128);
		for (final IToken token : tokens) {
			builder.append(separator);
			builder.append(token.toString());
		}
		return builder.length() == 0 ? "-" : builder.substring(separator.length());
	}

	public final IToken[] getArray() {
		return Check.notNull(tokens);
	}

}