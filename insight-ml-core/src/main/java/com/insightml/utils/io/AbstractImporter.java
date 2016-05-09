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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Samples;
import com.insightml.utils.Collections;
import com.insightml.utils.io.serialization.ISerializer;
import com.insightml.utils.types.AbstractModule;

public abstract class AbstractImporter<I extends ISample, E> extends AbstractModule {

	private final char separator;
	private final int numColumns;
	final boolean hasHeader;
	private final Logger logger = LoggerFactory.getLogger(AbstractImporter.class);

	public AbstractImporter(final char separator, final int numColumns, final boolean hasHeader) {
		this.separator = separator;
		this.numColumns = numColumns;
		this.hasHeader = hasHeader;
	}

	public final ISamples<I, E> unserializeOrImport(final File file, final ISerializer serializer) throws IOException {
		new File("tmp/").mkdir();
		final File fil = new File("tmp/samples_" + file.getName());
		if (!fil.exists()) {
			serializer.serialize(fil, run(IoUtils.reader(file)));
		}
		return serializer.unserialize(fil, Samples.class);
	}

	public final List<I> run(final Reader reader) {
		logger.info("Importing from " + reader);
		return Collections.merge(new CsvParser<List<I>>(separator, '"', 0, numColumns) {

			private String[] columnNames;

			@Override
			protected List<I> parse(final int lineNum, final String[] line) {
				if (hasHeader && columnNames == null) {
					columnNames = line;
					return null;
				}
				return importLine(lineNum, line, columnNames);
			}
		}.run(reader));
	}

	protected abstract List<I> importLine(int lineNum, String[] line, String[] columnNames);

}
