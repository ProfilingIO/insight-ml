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
package com.insightml;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.insightml.data.IDataset;
import com.insightml.data.SimpleDataset;
import com.insightml.data.samples.SimpleSample;
import com.insightml.data.utils.AnonymousSamplesReader;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.functions.ObjectiveFunctions;
import com.insightml.utils.io.IoUtils;
import com.insightml.utils.types.collections.PairList;

public final class TestDatasets {

	public static PairList<IDataset<?, ?>, ObjectiveFunction[]> createInstances() {
		final PairList<IDataset<?, ?>, ObjectiveFunction[]> instances = new PairList<>(true);
		instances.add(createNumeric(), ObjectiveFunctions.METRICS_NUMERIC);
		instances.add(createNominal(), ObjectiveFunctions.METRICS_NOMIAL);
		instances.add(createBoolean(), ObjectiveFunctions.METRICS_BINARY);
		// instances.put(createString().loadAll(),
		// IObjectiveFunction.METRICS_NOMIAL);
		return instances;
	}

	public static SimpleDataset<SimpleSample, Double> createNumeric() {
		try {
			return createNumeric(
					IoUtils.gzipReader(TestDatasets.class.getResourceAsStream("/data/winequality-white.csv.gz")), ';',
					12, 11);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static SimpleDataset<SimpleSample, Double> numericCommunities() {
		try {
			return createNumeric(
					IoUtils.gzipReader(TestDatasets.class.getResourceAsStream("/data/communities.data.gz")), ';', 102,
					101);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static SimpleDataset<SimpleSample, Double> createNumeric(final Reader reader, final char split,
			final int numColumns, final int labelIndex) {
		return SimpleDataset
				.create(new AnonymousSamplesReader<>(null, labelIndex, split, numColumns, false, SimpleSample.class)
						.run(reader));
	}

	public static SimpleDataset<SimpleSample, ?> createNumeric(final int numSamples, final int numFeatures) {
		final List<SimpleSample> list = new LinkedList<>();
		final Random rnd = new Random(0);
		final String[] names = new String[numFeatures];
		for (int j = 0; j < numFeatures; ++j) {
			names[j] = "f" + j;
		}
		for (int i = 0; i < numSamples; ++i) {
			final Double[] label = new Double[] { rnd.nextDouble() };
			final double[] f = new double[numFeatures];
			for (int j = 0; j < numFeatures; ++j) {
				f[j] = rnd.nextDouble();
			}
			list.add(new TestInstance(label, f, names));
		}
		return SimpleDataset.create(list);
	}

	public static SimpleDataset<SimpleSample, Double> createNominal() {
		final List<SimpleSample> list = new LinkedList<>();
		final Random rnd = new Random(0);
		for (int i = 0; i < 400; ++i) {
			final int random = rnd.nextInt(3);
			list.add(createInstance(
					new Double[] { random == 0 ? 1.0 : 0.0, random == 1 ? 1.0 : 0.0, random == 2 ? 1.0 : 0.0, }, random,
					rnd));
		}
		return SimpleDataset.create(list);
	}

	public static SimpleDataset<SimpleSample, Double> createBoolean() {
		final List<SimpleSample> list = new LinkedList<>();
		final Random rnd = new Random(0);
		for (int i = 0; i < 2000; ++i) {
			final double random = rnd.nextDouble();
			list.add(createInstance(new Double[] { random <= 0.5 ? 1.0 : 0.0 }, random * 10, rnd));
		}
		return SimpleDataset.create(list);
	}

	private static SimpleSample createInstance(final Object[] label, final double offset, final Random random) {
		return createInstance(label, offset + 7 + random.nextDouble(), offset + 3 + random.nextDouble(),
				offset + 5 + random.nextDouble(), offset + 1 + random.nextDouble(), offset + 9 + random.nextDouble());
	}

	private static SimpleSample createInstance(final Object[] label, final double... f) {
		final String[] names = new String[5];
		for (int i = 0; i < 5; ++i) {
			names[i] = "f" + (i + 1);
		}
		return new TestInstance(label, f, names);
	}
}
