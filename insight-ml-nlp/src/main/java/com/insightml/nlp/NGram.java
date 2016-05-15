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

import java.util.Arrays;

import com.insightml.utils.Check;

public final class NGram extends AbstractToken {
	private static final long serialVersionUID = -5823066688625491869L;

	private final Token[] tokens;

	public NGram(final Token[] tokens, final String original, final String pos) {
		super(original, isStemmed(tokens), pos);
		this.tokens = Check.size(tokens, 2, 15);
	}

	NGram(final String[] tokens, final String original) {
		super(original, false, null);
		if (tokens.length < 2 || tokens.length > 13) {
			throw new IllegalArgumentException("Too many or too few tokens: " + Arrays.toString(tokens));
		}
		final Token[] tkns = new Token[tokens.length];
		for (int i = 0; i < tkns.length; ++i) {
			tkns[i] = new Token(tokens[i]);
		}
		this.tokens = tkns;
	}

	private static boolean isStemmed(final Token[] tokens) {
		Boolean bool = null;
		for (final Token token : tokens) {
			if (bool == null) {
				bool = token.isStemmed();
			} else {
				Check.state(bool == token.isStemmed());
			}
		}
		return bool;
	}

	public Token[] getTokens() {
		return tokens;
	}

	@Override
	public int length() {
		int length = -1;
		for (final Token token : tokens) {
			length += token.length() + 1;
		}
		return length;
	}

	@Override
	public NGram toLowerCase() {
		final Token[] builder = new Token[tokens.length];
		for (int i = 0; i < tokens.length; ++i) {
			builder[i] = tokens[i].toLowerCase();
		}
		return new NGram(builder, getOriginal(), getPos());
	}

	@Override
	boolean equals(final IToken obj) {
		if (obj instanceof Token) {
			return false;
		}
		return Arrays.deepEquals(tokens, ((NGram) obj).tokens);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(32);
		for (final IToken token : tokens) {
			builder.append(' ');
			builder.append(token);
		}
		return builder.substring(1);
	}

}
