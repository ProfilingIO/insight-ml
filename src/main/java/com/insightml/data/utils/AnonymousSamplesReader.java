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
package com.insightml.data.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import com.insightml.data.samples.SimpleSample;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.io.AbstractImporter;
import com.insightml.utils.types.collections.FloatArray;
import com.insightml.utils.types.collections.IList;

public final class AnonymousSamplesReader<S extends SimpleSample> extends AbstractImporter<S, Double> {

	private int id;
	private final Integer idIndex;
	private final Integer labelIndex;
	private final boolean isBoolean;
	private String[] colNames;
	private final Class<S> clazz;

	public AnonymousSamplesReader(final Integer idIndex, final Integer labelIndex, final char separator,
			final int numColumns, final boolean isBoolean, final Class<S> clazz) {
		super(separator, Check.num(numColumns, labelIndex == null ? 2 : labelIndex + 1, 104), true);
		this.idIndex = idIndex;
		this.labelIndex = labelIndex;
		this.isBoolean = isBoolean;
		this.clazz = clazz;
	}

	@SuppressWarnings("null")
	@Override
	protected List<S> importLine(final int lineNum, final String[] line, final String[] columnNames) {
		final FloatArray features = new FloatArray(columnNames.length);
		final List<String> names = new LinkedList<>();
		for (int i = 0; i < line.length; ++i) {
			if ((idIndex == null || i != idIndex) && (labelIndex == null || i != labelIndex)) {
				Check.notNull(line[i], columnNames[i] + " not set in line " + lineNum + ".");
				features.add(Float.parseFloat(line[i]));
				names.add(columnNames[i]);
			}
		}
		final Object label = labelIndex == null ? null
				: isBoolean ? line[labelIndex].equals("1") : Double.valueOf(line[labelIndex]);
		if (colNames == null) {
			colNames = Arrays.of(names);
		}
		try {
			final S sample = (S) clazz.getConstructors()[0].newInstance(idIndex == null ? ++id
					: Integer.parseInt(line[idIndex]), new Object[] { label, }, features.toArray(), colNames);
			return new IList<>(sample);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			throw new IllegalStateException(e);
		}
	}
}
