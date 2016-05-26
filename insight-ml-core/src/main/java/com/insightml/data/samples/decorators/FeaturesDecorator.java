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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.Normalization;
import com.insightml.utils.Compare;
import com.insightml.utils.StrictComparator;
import com.insightml.utils.jobs.ParallelFor;

public final class FeaturesDecorator<S extends Sample, E> extends AbstractDecorator<S, E> {

	private String[] featureNames;
	double[][] features;
	private int[][] orderedByFeatures;

	public FeaturesDecorator(final ISamples<S, E> orig, final double[][] features, final String[] featureNames) {
		super(orig);
		this.features = features;
		this.featureNames = featureNames;
	}

	public FeaturesDecorator(final ISamples<S, E> orig, final IFeatureProvider prov, final IFeatureFilter featureFilter,
			final Normalization normalization, final boolean isTraining) {
		super(orig);

		featureNames = featureFilter.allowedFeatures(prov.featureNames(orig));

		features = new double[orig.size()][];
		ParallelFor.run(i -> {
			final S sample = orig.get(i);
			features[i] = sample == null ? null
					: Preconditions.checkNotNull(prov.features(sample, featureNames(), isTraining));
			return 1;
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
					ParallelFor.run(f -> {
						final List<OrderItem> order = new ArrayList<>(size);
						for (int i = 0; i < size; ++i) {
							order.add(new OrderItem(i));
						}
						Collections.sort(order, new StrictComparator<OrderItem>() {
							@Override
							public int comp(final OrderItem o1, final OrderItem o2) {
								final int comp = Compare.compareDouble(features[o1.index][f], features[o2.index][f]);
								return comp == 0 ? Compare.compareInt(o1.index, o2.index) : comp;
							}
						});
						ordered[f] = new int[size];
						int i = -1;
						for (final OrderItem tuple : order) {
							ordered[f][++i] = tuple.index;
						}
						return "";
					}, 0, featureNames.length, 1);
					orderedByFeatures = ordered;
				}
			}
		}
		return orderedByFeatures;
	}

	private static final class OrderItem {
		final int index;

		OrderItem(final int index) {
			this.index = index;
		}
	}

}
