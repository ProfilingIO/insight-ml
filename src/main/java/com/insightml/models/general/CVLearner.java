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
package com.insightml.models.general;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.insightml.data.samples.Sample;
import com.insightml.models.AbstractLearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Check;
import com.insightml.utils.Maps;
import com.insightml.utils.Strings;
import com.insightml.utils.io.AbstractImporter;
import com.insightml.utils.io.IoUtils;

public final class CVLearner extends AbstractLearner<Sample, Object, Double> {

	private final String mainFile;
	final Map<Integer, Double>[] predictions;

	public CVLearner(final String file, final int labels) throws IOException {
		super(null);
		mainFile = Strings.substringAfterLast(file, '/');
		predictions = new Map[labels];
		for (int i = 0; i < predictions.length; ++i) {
			predictions[i] = Maps.create(1024);
		}
		loadFile(new File(file));
	}

	@Override
	public String getName() {
		return mainFile.replace(".csv", "");
	}

	private void loadFile(final File file) throws IOException {
		new AbstractImporter(',', 0, true) {
			@Override
			protected List<?> importLine(final int lineNum, final String[] line, final String[] columnNames) {
				final int instance = Integer.parseInt(line[0]);
				final int mult = line.length <= predictions.length + 2 ? 1 : 2;
				final int offset = line.length - predictions.length * mult;
				for (int i = 0; i < predictions.length; ++i) {
					if (line[offset + i * mult] == null) {
						continue;
					}
					final Serializable previous = predictions[i].put(instance, Double.valueOf(line[offset + i * mult]));
					if (previous != null) {
						throw new IllegalStateException();
					}
				}
				return Collections.emptyList();
			}
		}.run(IoUtils.reader(file));
	}

	@Override
	public IModel<Sample, Double> run(final LearnerInput<? extends Sample, ? extends Object> input) {
		Check.isNull(input.valid);
		return new PriorLearner<>(toString().replace(',', '-'), predictions[input.labelIndex]).run(input);
	}

}
