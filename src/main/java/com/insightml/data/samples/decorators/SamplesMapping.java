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

import java.io.Serial;
import java.lang.reflect.Array;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;

/**
 * A decorator that provides a view on a subset or a reordered version of the original samples. It uses an index mapping
 * to determine which samples from the original data are included and in which order. This can be used for tasks like
 * train/test splitting, bootstrapping, or oversampling.
 *
 * @param <S>
 * 		The type of the samples.
 * @param <E>
 * 		The type of the expected values (labels).
 */
public final class SamplesMapping<S extends Sample, E> extends AbstractDecorator<S, E> {
	@Serial
	private static final long serialVersionUID = 5410695563675584751L;

	/**
	 * An array where each element is an index into the original samples. indexMapping[i] = originalIndex
	 */
	private final int[] indexMapping;

	/**
	 * Creates a new samples mapping.
	 *
	 * @param original
	 * 		The original samples to map from.
	 * @param indexMapping
	 * 		The mapping of indices.
	 */
	public SamplesMapping(final ISamples<S, E> original, final int[] indexMapping) {
		super(original);
		this.indexMapping = indexMapping;
	}

	@Override
	protected int getInstance(final int i) {
		return indexMapping[i];
	}

	@Override
	public int size() {
		return indexMapping.length;
	}

	@Override
	public E[] expected(final int labelIndex) {
		final E[] refExpected = ref.expected(labelIndex);
		if (refExpected == null) {
			return null;
		}

		final E[] mappedExpected = (E[]) Array.newInstance(refExpected.getClass().getComponentType(),
				indexMapping.length);
		for (int i = 0; i < indexMapping.length; ++i) {
			mappedExpected[i] = refExpected[indexMapping[i]];
		}
		return mappedExpected;
	}

	@Override
	public double[] weights(final int labelIndex) {
		final double[] weightsRef = ref.weights(labelIndex);
		if (weightsRef == null) {
			return null;
		}

		final double[] mappedWeights = new double[indexMapping.length];
		for (int i = 0; i < indexMapping.length; ++i) {
			mappedWeights[i] = weightsRef[indexMapping[i]];
		}
		return mappedWeights;
	}

	public int[] getIndexMap() {
		return indexMapping;
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
		final float[][] refFeatures = ref.features();
		if (refFeatures == null) {
			return null;
		}

		final float[][] mappedFeatures = new float[indexMapping.length][];
		for (int i = 0; i < indexMapping.length; ++i) {
			mappedFeatures[i] = refFeatures[indexMapping[i]];
		}
		return mappedFeatures;
	}

	/**
	 * Computes the ordered indexes for each feature. The algorithm maps the original ordered indexes (from the parent)
	 * to the new indexes in this view. It handles cases where an original index appears multiple times in the mapping
	 * (oversampling).
	 */
	@Override
	public int[][] orderedIndexes() {
		final int[][] parentOrdered = ref.orderedIndexes();
		if (parentOrdered == null) {
			return null;
		}

		// To efficiently map original indexes to new indexes, we build a linked-list structure.
		// firstOccurrence[origIndex] stores the first position in indexMapping where origIndex appears.
		final int[] firstOccurrence = new int[ref.size()];
		java.util.Arrays.fill(firstOccurrence, -1);
		// nextOccurrence[mappedIndex] stores the next position in indexMapping where the same origIndex appears.
		final int[] nextOccurrence = new int[indexMapping.length];

		// Fill the linked-list structure by iterating backwards through the index mapping.
		for (int i = indexMapping.length - 1; i >= 0; --i) {
			final int origIndex = indexMapping[i];
			nextOccurrence[i] = firstOccurrence[origIndex];
			firstOccurrence[origIndex] = i;
		}

		final int numFeatures = numFeatures();
		final int numSamples = indexMapping.length;
		final int[][] mappedOrdered = new int[numFeatures][numSamples];

		for (int f = 0; f < numFeatures; ++f) {
			int currentMappedPos = 0;
			// Iterate through the original sorted order of samples for this feature.
			for (final int origIndex : parentOrdered[f]) {
				// For each occurrence of this original sample in our mapping, add it to the new sorted order.
				int mappedIndex = firstOccurrence[origIndex];
				while (mappedIndex != -1) {
					mappedOrdered[f][currentMappedPos++] = mappedIndex;
					mappedIndex = nextOccurrence[mappedIndex];
				}
			}
		}
		return mappedOrdered;
	}

}
