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
package com.insightml.models;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;

public abstract class AbstractIndependentFeaturesModel extends AbstractIndependentModel<Sample, Double>
		implements DoubleModel {

	private static final long serialVersionUID = -1870885956526569825L;

	protected AbstractIndependentFeaturesModel() {
	}

	public AbstractIndependentFeaturesModel(final String[] features) {
		super(Preconditions.checkNotNull(features));
	}

	@Override
	protected final Double predict(final int instance, final ISamples<? extends Sample, ?> instances,
			final int[] featuresFilter) {
		return _predict(instance, instances.features(), featuresFilter);
	}

	@Override
	public final double[] predictDouble(final ISamples<? extends Sample, ?> instances) {
		final int[] featuresFilter = constractFeaturesFilter(instances);
		final double[][] instancesFeatures = instances.features();
		final double[] result = new double[instancesFeatures.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = _predict(i, instancesFeatures, featuresFilter);
		}
		return result;
	}

	private double _predict(final int instance, final double[][] instancesFeatures,
			final @Nullable int[] featuresFilter) {
		if (featuresFilter == null) {
			return predict(instancesFeatures[instance]);
		}
		return predict(selectFeatures(instance, instancesFeatures, featuresFilter));
	}

	protected static double[] selectFeatures(final int instance, final double[][] instancesFeatures,
			final int[] featuresFilter) {
		final double[] features = instancesFeatures[instance];
		final double[] selection = new double[featuresFilter.length];
		for (int i = 0; i < selection.length; ++i) {
			selection[i] = featuresFilter[i] == -1 ? 0 : features[featuresFilter[i]];
		}
		return selection;
	}

	public abstract double predict(double[] features);

}
