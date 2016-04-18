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

import com.insightml.data.samples.decorators.FeaturesFilterDecorator;
import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.math.Vectors;
import com.insightml.utils.Check;
import com.insightml.utils.Pair;
import com.insightml.utils.types.collections.IntArray;

public abstract class AbstractSamples<S extends ISample, E> implements ISamples<S, E> {

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

	@Override
	public Iterator<S> iterator() {
		throw new UnsupportedOperationException();
	}

}
