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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.Normalization;
import com.insightml.utils.jobs.ParallelFor;

public final class FeaturesDecorator<S extends Sample, E> extends AbstractDecorator<S, E> {
	private static final Logger LOG = LoggerFactory.getLogger(FeaturesDecorator.class);

	private String[] featureNames;
	double[][] features;
	private int[][] orderedByFeatures;

	public FeaturesDecorator(final ISamples<S, E> orig, final double[][] features, final String[] featureNames) {
		super(orig);
		this.features = features;
		this.featureNames = featureNames;
	}

	public FeaturesDecorator(final ISamples<S, E> orig, final IFeatureProvider prov, final String[] featureNames,
			final Normalization normalization, final boolean isTraining) {
		super(orig);

		this.featureNames = featureNames;

		features = new double[orig.size()][];
		ParallelFor.run(i -> {
			try {
				final S sample = orig.get(i);
				features[i] = sample == null ? null
						: Preconditions.checkNotNull(prov.features(sample, featureNames, isTraining));
				return 1;
			} catch (final Throwable e) {
				LOG.error("{}", e, e);
				throw e;
			}
		}, 0, features.length, 100);
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
	public double[][] features() {
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
					for (int f = 0; f < featureNames.length; ++f) {
						ordered[f] = new int[size];
						for (int i = 0; i < size; ++i) {
							ordered[f][i] = i;
						}
						quickSort(ordered[f], 0, size - 1, f);
					}
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

}
