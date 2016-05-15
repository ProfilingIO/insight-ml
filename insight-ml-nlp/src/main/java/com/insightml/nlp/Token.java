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
import java.util.Locale;

import com.insightml.utils.Check;

public final class Token extends AbstractToken {
	private static final long serialVersionUID = -4879333234810376325L;

	private char[] token;

	public Token(final String token) {
		this(token, null);
	}

	public Token(final String token, final String pos) {
		super(null, false, pos);
		init(token);
	}

	public Token(final String token, final String original, final boolean isStemmed, final String pos) {
		super(Check.length(original, 1, 999), isStemmed, pos);
		init(token);
	}

	private void init(final String tokn) {
		Check.num(tokn.length(), 1, 47, tokn);
		token = new char[tokn.length()];
		for (int i = 0; i < token.length; ++i) {
			token[i] = tokn.charAt(i);
		}
	}

	public static IToken of(final String string) {
		if (string == null) {
			return null;
		}
		final String trimmed = string.trim();
		Check.argument(!trimmed.isEmpty());
		final String[] ngrams = trimmed.split(" +");
		return ngrams.length == 1 ? new Token(ngrams[0]) : new NGram(ngrams, trimmed);
	}

	@Override
	public Token toLowerCase() {
		final String lc = toString().toLowerCase(Locale.ENGLISH);
		return isStemmed() ? new Token(lc, getOriginal(), isStemmed(), getPos()) : new Token(lc);
	}

	public char charAt(final int i) {
		return token[i];
	}

	@Override
	public int length() {
		return token.length;
	}

	@Override
	boolean equals(final IToken obj) {
		if (obj == null || obj instanceof NGram) {
			return false;
		}
		return Arrays.equals(token, ((Token) obj).token);
	}

	@Override
	public String toString() {
		return new String(token);
	}

}
