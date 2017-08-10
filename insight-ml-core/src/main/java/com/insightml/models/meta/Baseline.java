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
package com.insightml.models.meta;

import java.io.Serializable;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.types.SumMap;
import com.insightml.models.DoubleModel;

public final class Baseline implements DoubleModel, Serializable {
	private static final long serialVersionUID = -319726204965685995L;

	private double value;

	Baseline() {
	}

	public Baseline(final double value) {
		this.value = value;
	}

	@Override
	public double[] predictDouble(final ISamples<? extends Sample, ?> instances) {
		final double[] preds = new double[instances.size()];
		for (int i = 0; i < preds.length; ++i) {
			preds[i] = value;
		}
		return preds;
	}

	@Override
	public double predict(final double[] features, final int[] featuresFilter) {
		return value;
	}

	@Override
	public int[] constractFeaturesFilter(final ISamples<? extends Sample, ?> instances) {
		return null;
	}

	@Override
	public SumMap<String> featureImportance() {
		return null;
	}
}