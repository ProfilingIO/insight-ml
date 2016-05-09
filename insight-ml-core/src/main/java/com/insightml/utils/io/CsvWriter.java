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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.SimpleFormatter;

public final class CsvWriter extends AbstractClass implements AutoCloseable {
	private final BufferedWriter builder;
	private final String[] expectedColumns;
	private final char separator;

	public CsvWriter(final File targetFile, final char separator, final boolean useHeader, final String... columns) {
		this.separator = separator;
		try {
			builder = new BufferedWriter(new FileWriter(targetFile));
			if (useHeader) {
				boolean init = false;
				for (final String column : columns) {
					if (init) {
						builder.append(separator);
					} else {
						init = true;
					}
					builder.append(Check.notNull(column));
				}
				builder.append('\n');
			}
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		expectedColumns = Check.size(columns, 1, 1500);
	}

	public void addLine(final Map<? extends CharSequence, ? extends Object> columns) {
		final StringBuilder line = new StringBuilder();
		for (final String expected : expectedColumns) {
			line.append(columns.get(expected) + "" + separator);
		}
		line.replace(line.length() - 1, line.length(), "\n");
		try {
			builder.append(line.toString());
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String formatLine(final Object... line) {
		final StringBuilder str = new StringBuilder();
		final SimpleFormatter formatter = new SimpleFormatter();
		for (int i = 0; i < line.length; ++i) {
			if (i > 0) {
				str.append(';');
			}
			str.append('"');
			if (line[i] == null) {
				str.append(0);
			} else if (line[i] instanceof Double) {
				final String val = formatter.format((double) line[i]).replace('.', ',');
				str.append(val.startsWith(",") ? "0" + val : val);
			} else {
				str.append(line[i]);
			}
			str.append('"');
		}
		return str.toString();
	}

	@Override
	public void close() {
		try {
			builder.close();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
