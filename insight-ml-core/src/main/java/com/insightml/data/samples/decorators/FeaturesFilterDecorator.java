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

import com.insightml.data.samples.Sample;
import com.insightml.data.samples.ISamples;
import com.insightml.math.Vectors;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;

public final class FeaturesFilterDecorator<S extends Sample, E> extends AbstractDecorator<S, E> {

	private final boolean[] filter;
	private final int[] map;
	private int[][] orderedIndexes;

	public FeaturesFilterDecorator(final ISamples<S, E> orig, final boolean[] filter) {
		super(orig);
		Check.equals(orig.numFeatures(), filter.length, "features");
		this.filter = filter;
		this.map = new int[Vectors.sum(filter)];
		for (int i = 0, j = -1; i < filter.length; ++i) {
			if (filter[i]) {
				map[++j] = i;
			}
		}
	}

	@Override
	protected int getInstance(final int i) {
		return i;
	}

	@Override
	public int size() {
		return ref.size();
	}

	@Override
	public E[] expected(final int labelIndex) {
		return ref.expected(labelIndex);
	}

	@Override
	public double[] weights(final int labelIndex) {
		return ref.weights(labelIndex);
	}

	@Override
	public int numFeatures() {
		return map.length;
	}

	@Override
	public String[] featureNames() {
		return Arrays.filter(ref.featureNames(), filter);
	}

	@Override
	public double[][] features() {
		final double[][] parent = ref.features();
		final double[][] fil = new double[size()][];
		for (int i = 0; i < fil.length; ++i) {
			fil[i] = Arrays.filter(parent[i], filter);
		}
		return fil;
	}

	@Override
	public int[][] orderedIndexes() {
		if (orderedIndexes == null) {
			final int[][] parent = ref.orderedIndexes();
			orderedIndexes = new int[map.length][];
			for (int i = 0; i < orderedIndexes.length; ++i) {
				orderedIndexes[i] = parent[map[i]];
			}
		}
		return orderedIndexes;
	}

}
