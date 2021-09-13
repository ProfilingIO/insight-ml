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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.samples.AbstractSamples;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.IArguments;
import com.insightml.utils.jobs.ParallelFor;

public class FeaturesDecorator<S extends Sample, E> extends AbstractSamples<S, E> {
	private static final long serialVersionUID = -2321252279857532465L;

	private ISamples<S, E> ref;
	private String[] featureNames;
	private float[][] features;
	private int[][] orderedByFeatures;

	FeaturesDecorator() {
	}

	public FeaturesDecorator(final ISamples<S, E> orig, final float[][] features, final String[] featureNames) {
		this.ref = orig;
		this.features = features;
		this.featureNames = featureNames;
	}

	public FeaturesDecorator(final ISamples<S, E> orig, final IFeatureProvider prov, final String[] featureNames,
			final Map<String, Stats> featureStats, final boolean isTraining, final IArguments arguments) {
		this.ref = orig;

		this.featureNames = featureNames;

		features = new float[orig.size()][];
		ParallelFor.run(i -> {
			final S sample = orig.get(i);
			features[i] = sample == null ? null
					: Preconditions
							.checkNotNull(prov.features(sample, featureNames, featureStats, isTraining, arguments));
			return 1;
		}, 0, features.length, 950);
	}

	@Override
	public int size() {
		return features.length;
	}

	@Override
	public S get(final int i) {
		return ref.get(i);
	}

	@Override
	public int getId(final int i) {
		return ref.getId(i);
	}

	@Override
	public int numLabels() {
		return ref.numLabels();
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
	public float[][] features() {
		return features;
	}

	@Override
	public String[] featureNames() {
		return featureNames;
	}

	@Override
	public int numFeatures() {
		return featureNames.length;
	}

	@Override
	public int[][] orderedIndexes() {
		if (orderedByFeatures == null) {
			synchronized (featureNames) {
				if (orderedByFeatures == null) {
					final int size = size();
					final int[][] ordered = new int[featureNames.length][];
					ParallelFor.run(f -> {
						ordered[f] = new int[size];
						for (int i = 0; i < size; ++i) {
							ordered[f][i] = i;
						}
						quickSort(ordered[f], 0, size - 1, f);
						return null;
					}, 0, featureNames.length, 20);
					orderedByFeatures = ordered;
				}
			}
		}
		return orderedByFeatures;
	}

	private void quickSort(final int[] idx, final int lo, final int hi, final int f) {
		final int partition = partition(idx, lo, hi, f);
		if (lo < partition) {
			quickSort(idx, lo, partition, f);
		}
		if (partition + 1 < hi) {
			quickSort(idx, partition + 1, hi, f);
		}
	}

	private int partition(final int[] idx, final int lo, final int hi, final int f) {
		final double pivot = features[idx[lo + (hi - lo) / 2]][f];
		int i = lo - 1;
		int j = hi + 1;
		while (true) {
			do {
				++i;
			} while (features[idx[i]][f] < pivot);
			do {
				--j;
			} while (features[idx[j]][f] > pivot);
			if (i >= j) {
				return j;
			}
			final int tmp = idx[i];
			idx[i] = idx[j];
			idx[j] = tmp;
		}
	}

	@Override
	public final SamplesMapping<S, E> randomize(final Random random) {
		throw new IllegalAccessError(this + "");
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.deepHashCode(features), expected(0));
	}
}
