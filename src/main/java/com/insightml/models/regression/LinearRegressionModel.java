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
package com.insightml.models.regression;

import com.insightml.models.AbstractIndependentFeaturesModel;
import com.insightml.utils.Check;
import com.insightml.utils.ui.UiUtils;

public final class LinearRegressionModel extends AbstractIndependentFeaturesModel {

	private static final long serialVersionUID = -2883586613784551270L;

	private final double[] coefficients;
	private final boolean bias;

	public LinearRegressionModel(final double[] coefficients, final String[] featureNames) {
		super(featureNames);
		Check.num(coefficients.length, featureNames.length, featureNames.length + 1);
		this.coefficients = coefficients;
		bias = coefficients.length > featureNames.length;
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	@Override
	public double predict(final double[] features) {
		double result = bias ? coefficients[0] : 0;
		for (int i = 0; i < features.length; ++i) {
			result += coefficients[i + (bias ? 1 : 0)] * features[i];
		}
		return result;
	}

	@Override
	public String info() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 1; i < coefficients.length; ++i) {
			builder.append(UiUtils.format(coefficients[i]) + "*" + features()[i - 1] + " + ");
		}
		builder.append(UiUtils.format(coefficients[0]));
		return builder.toString();
	}

}
