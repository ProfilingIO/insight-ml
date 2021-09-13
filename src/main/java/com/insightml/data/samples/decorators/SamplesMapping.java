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
package com.insightml.data.samples.decorators;

import java.lang.reflect.Array;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;

public final class SamplesMapping<S extends Sample, E> extends AbstractDecorator<S, E> {
	private static final long serialVersionUID = 5410695563675584751L;

	final int[] map;
	private final float[][] features;
	private E[][] exp;
	private final double[][] weights;
	private int[][] orderedIndexes;

	public SamplesMapping(final ISamples<S, E> orig, final int[] map) {
		super(orig);
		this.map = map;

		final float[][] refFeats = ref.features();
		features = refFeats == null ? null : new float[map.length][];
		weights = new double[ref.numLabels()][map.length];

		for (int labelIndex = 0; labelIndex < weights.length; ++labelIndex) {
			final E[] refExpected = ref.expected(labelIndex);
			final double[] weightsRef = ref.weights(labelIndex);

			if (labelIndex == 0) {
				exp = refExpected == null ? null
						: (E[][]) Array.newInstance(refExpected[0].getClass(), weights.length, map.length);
			}

			for (int i = 0; i < map.length; ++i) {
				if (features != null && refFeats != null) {
					features[i] = refFeats[map[i]];
				}
				if (exp != null && refExpected != null) {
					exp[labelIndex][i] = refExpected[map[i]];
				}
				weights[labelIndex][i] = weightsRef[map[i]];
			}
		}
	}

	@Override
	protected int getInstance(final int i) {
		return map[i];
	}

	@Override
	public int size() {
		return map.length;
	}

	@Override
	public E[] expected(final int labelIndex) {
		return exp == null ? null : exp[labelIndex];
	}

	@Override
	public double[] weights(final int labelIndex) {
		return weights[labelIndex];
	}

	@Override
	public int numFeatures() {
		return ref.numFeatures();
	}

	@Override
	public String[] featureNames() {
		return ref.featureNames();
	}

	@Override
	public float[][] features() {
		return features;
	}

	public int[] getIndexMap() {
		return map;
	}

	@Override
	public synchronized int[][] orderedIndexes() {
		if (orderedIndexes == null) {
			final int[] reverse = new int[ref.size()];
			for (int i = 0; i < map.length; ++i) {
				reverse[map[i]] = i + 1;
			}
			final int[][] parent = ref.orderedIndexes();
			orderedIndexes = new int[numFeatures()][map.length];
			for (int f = 0; f < orderedIndexes.length; ++f) {
				int j = -1;
				for (final int i : parent[f]) {
					if (reverse[i] != 0) {
						orderedIndexes[f][++j] = reverse[i] - 1;
					}
				}
			}
		}
		return orderedIndexes;
	}

}
