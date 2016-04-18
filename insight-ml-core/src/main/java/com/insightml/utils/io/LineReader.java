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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.insightml.utils.Check;

public final class LineReader implements Iterator<String>, Iterable<String> {

	private final BufferedReader br;

	private boolean calledNext;
	private String nextLine;

	public LineReader(final File file) {
		this(inputStream(file));
	}

	private static InputStream inputStream(final File file) {
		try {
			return file.getName().endsWith(".gz") ? new GZIPInputStream(new FileInputStream(file))
					: new FileInputStream(file);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public LineReader(final InputStream file) {
		try {
			br = new BufferedReader(new InputStreamReader(Check.notNull(file), "UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean hasNext() {
		calledNext = true;
		try {
			nextLine = br.readLine();
			if (nextLine == null) {
				close();
				return false;
			}
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			close();
			return false;
		}
	}

	@Override
	public String next() {
		if (calledNext) {
			calledNext = false;
			return nextLine;
		}
		try {
			return br.readLine();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public LineReader skipLines(final int amount) {
		try {
			for (int i = 0; i < amount; ++i) {
				br.readLine();
			}
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		return this;
	}

	private void close() {
		try {
			br.close();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void remove() {
		throw new IllegalAccessError();
	}

	public List<String> all() {
		final List<String> all = new LinkedList<>();
		for (final String line : this) {
			all.add(line);
		}
		return all;
	}

	@Override
	public Iterator<String> iterator() {
		return this;
	}

}