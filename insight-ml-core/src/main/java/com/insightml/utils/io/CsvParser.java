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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.insightml.utils.Check;
import com.insightml.utils.Strings;
import com.insightml.utils.types.AbstractClass;

public class CsvParser<T> extends AbstractClass {

	final char splitChar;
	final char closeChar;

	final int skipLines;
	final int readLines;
	final int numColumns;

	public CsvParser(final char splitChar, final int numColumns) {
		this(splitChar, 0, numColumns);
	}

	public CsvParser(final char splitChar, final int skipLines, final int numColumns) {
		this(splitChar, '"', skipLines, numColumns);
	}

	public CsvParser(final char splitChar, final char closeChar, final int skipLines, final int numColumns) {
		this.splitChar = splitChar;
		this.closeChar = closeChar;
		this.skipLines = skipLines;
		readLines = Integer.MAX_VALUE;
		this.numColumns = Check.num(numColumns, -1, 104);
	}

	public final Iterable<String[]> iterator(final File file) throws IOException {
		return iterator(new InputStreamReader(file.getName().endsWith(".gz")
				? new GZIPInputStream(new FileInputStream(file)) : new FileInputStream(file)));
	}

	public final Iterable<String[]> iterator(final Reader reader) {
		return () -> {
			final It it = new It(reader);
			for (int i = 0; i < skipLines; ++i) {
				it.next();
			}
			return it;
		};
	}

	public Iterable<T> run(final Reader reader) {
		final Iterator<String[]> iterator = iterator(reader).iterator();
		final List<T> tasks = new LinkedList<>();
		for (int i = 1; iterator.hasNext(); ++i) {
			final T line = parse(i, iterator.next());
			if (line != null) {
				tasks.add(line);
			}
		}
		return tasks;
	}

	protected T parse(final int lineNum, final String[] line) {
		throw new IllegalAccessError(lineNum + ", " + line);
	}

	private final class It extends AbstractLinesIterator<String[]> {

		It(final Reader reader) {
			super(reader, readLines);
		}

		@Override
		String[] process(final BufferedReader reader) throws IOException {
			if (numColumns == -1) {
				return reader.readLine().split(splitChar + "");
			}
			final String[] entry = new String[numColumns];
			StringBuilder column = new StringBuilder(32);
			boolean open = false;
			try {
				for (int c = 0; c < numColumns;) {
					final int read = reader.read();
					final char chr = (char) read;
					if (chr == splitChar || !open && (chr == '\n' || read == -1)) {
						if (!open) {
							final String col = column.length() == 0 ? null : column.toString();
							if (col == null || "NULL".equals(col) || "NA".equals(col) || "NaN".equals(col)
									|| "".equals(entry[c]) || "?".equals(col)) {
								entry[c++] = null;
							} else {
								entry[c++] = Strings.removeStart(Strings.removeEnd(col, '"'), '"');
							}
							column = new StringBuilder(32);
						}
					} else if (chr == closeChar) {
						open = !open;
					} else {
						column.append(chr);
					}
				}
				return entry;
			} catch (final IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
