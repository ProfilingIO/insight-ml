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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.decorators.FeaturesFilterDecorator;
import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.math.Vectors;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.collections.IntArray;

public abstract class AbstractSamples<S extends Sample, E> extends AbstractClass implements ISamples<S, E> {
	private static final long serialVersionUID = 7627952745618197898L;

	@Override
	public final SamplesMapping<S, E> subset(final int[] indexes) {
		return new SamplesMapping<>(this, indexes);
	}

	@Override
	public final SamplesMapping<S, E> subset(final int from, final int to) {
		final int[] indexes = new int[to - from];
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i + from;
		}
		return subset(indexes);
	}

	@Override
	public final Pair<ISamples<S, E>, ISamples<S, E>> sample(final double ratio, final Random random) {
		Check.num(ratio, 0.01, 0.95);
		final int n = size();
		if (featureNames() == null) {
			final List<S> list = new LinkedList<>();
			for (int i = 0; i < n; ++i) {
				if (random.nextDouble() <= ratio) {
					list.add(get(i));
				}
			}
			return new Pair<>(new Samples<>(list), null);
		}
		final IntArray list = new IntArray(n);
		for (int i = 0; i < n; ++i) {
			if (random.nextDouble() <= ratio) {
				list.add(i);
			}
		}
		return new Pair<>(subset(list.toArray()), null);
	}

	@Override
	public final ISamples<S, E> filterFeatures(final boolean[] keep) {
		if (Vectors.sum(keep) == keep.length) {
			return this;
		}
		return new FeaturesFilterDecorator<>(this, keep);
	}

	@Override
	public final ISamples<S, E> sampleFeatures(final double ratio, final Random random) {
		if (ratio >= 1) {
			return this;
		}
		final boolean[] keep = new boolean[numFeatures()];
		for (int i = 0; i < keep.length; ++i) {
			keep[i] = random.nextDouble() <= ratio;
		}
		return filterFeatures(keep);
	}

	public final SamplesMapping<S, E> samplesWithLabelSet(final int labelIndex) {
		final E[] expected = expected(labelIndex);
		final double[] weights = weights(labelIndex);
		final IntArray indexes = new IntArray(weights.length);
		for (int i = 0; i < weights.length; ++i) {
			if (expected[i] != null && weights[i] > 0) {
				indexes.add(i);
			}
		}
		return subset(indexes.toArray());
	}

	@Override
	public Iterator<S> iterator() {
		return new SamplesIterator<>(this);
	}

	static final class SamplesIterator<S extends Sample> implements Iterator<S> {
		private final ISamples<S, ?> samples;

		private int i = 0;

		SamplesIterator(final ISamples<S, ?> samples) {
			this.samples = samples;
		}

		@Override
		public boolean hasNext() {
			return i + 1 <= samples.size();
		}

		@Override
		public S next() {
			final S next = samples.get(i);
			++i;
			return next;
		}
	}
}
