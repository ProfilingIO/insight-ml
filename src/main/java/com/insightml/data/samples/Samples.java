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
package com.insightml.data.samples;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.types.collections.ArrayIterator;

import jakarta.annotation.Nonnull;

public final class Samples<S extends Sample, E> extends AbstractSamples<S, E> {

	private static final long serialVersionUID = 3351881040269525917L;

	private S[] samples;
	private E[][] expected;
	private double[][] weights;

	public Samples() {
	}

	public Samples(final Iterable<S> samples) {
		this(samples, true);
	}

	public Samples(final Iterable<S> samples, final boolean storeLabels) {
		this.samples = Check.notNull(Arrays.of(samples));

		if (storeLabels) {
			final Class<E> labelClass = labelClass(this.samples);
			final int numLabels = numLabels(this.samples);
			expected =
					labelClass == null ? null : (E[][]) Array.newInstance(labelClass, numLabels, this.samples.length);
			weights = new double[numLabels == 0 ? 1 : numLabels][this.samples.length];

			for (int i = 0; i < this.samples.length; ++i) {
				final S sample = this.samples[i];
				final E[] exp = sample.getExpected();
				for (int j = 0; j < exp.length; ++j) {
					if (expected != null) {
						expected[j][i] = exp[j];
					}
					weights[j][i] = sample.getWeight(j);
				}
			}
		}
	}

	private Class<E> labelClass(final S[] instances) {
		for (final S inst : instances) {
			if (inst != null) {
				final E[] exp = inst.getExpected();
				if (exp != null && exp.length > 0 && exp[0] != null) {
					return (Class<E>) exp[0].getClass();
				}
			}
		}
		return null;
	}

	private int numLabels(final S[] instances) {
		int max = 0;
		for (final S inst : instances) {
			if (inst != null) {
				final E[] exp = inst.getExpected();
				if (exp != null) {
					max = Math.max(max, exp.length);
				}
			}
		}
		return max;
	}

	@Override
	public S get(final int i) {
		return samples[i];
	}

	@Override
	public int getId(final int i) {
		return samples[i].getId();
	}

	@Nonnull
	@Override
	public Iterator<S> iterator() {
		return new ArrayIterator<>(samples);
	}

	@Override
	public int size() {
		return samples.length;
	}

	@Override
	public int numLabels() {
		return weights.length;
	}

	@Override
	public E[] expected(final int labelIndex) {
		return expected == null ? null : expected[labelIndex];
	}

	@Override
	public double[] weights(final int labelIndex) {
		return weights == null ? null : weights[labelIndex];
	}

	@Override
	public float[][] features() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] featureNames() {
		throw new IllegalAccessError();
	}

	@Override
	public int numFeatures() {
		throw new IllegalAccessError();
	}

	@Override
	public int[][] orderedIndexes() {
		throw new IllegalAccessError();
	}

	@Override
	public SamplesMapping<S, E> randomize(final Random random) {
		final List<Integer> shuffled = new ArrayList<>(size());
		for (int i = 0; i < size(); ++i) {
			shuffled.add(i);
		}
		Collections.shuffle(shuffled, random);
		final int[] indexes = new int[shuffled.size()];
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = shuffled.get(i);
		}
		return new SamplesMapping<>(this, indexes);
	}

}
