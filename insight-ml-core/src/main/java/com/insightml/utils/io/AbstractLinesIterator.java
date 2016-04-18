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
package com.insightml.utils.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public abstract class AbstractLinesIterator<R> implements Iterator<R> {

	private int i;
	private final int readLines;
	private final BufferedReader br;

	protected AbstractLinesIterator(final Reader reader, final int readLines) {
		this.readLines = readLines;
		br = new BufferedReader(reader);
	}

	@Override
	public final boolean hasNext() {
		try {
			final boolean hasNext = i < readLines && br.ready();
			if (!hasNext) {
				br.close();
			}
			return hasNext;
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public final R next() {
		++i;
		try {
			return process(br);
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	abstract R process(BufferedReader reader) throws IOException;

	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}
}
