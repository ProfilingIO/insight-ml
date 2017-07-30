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

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import com.insightml.data.samples.Sample;
import com.insightml.models.AbstractBasicDoubleLearner;
import com.insightml.models.IModel;
import com.insightml.utils.Arguments;

public final class OLS extends AbstractBasicDoubleLearner {

	public OLS() {
		super(new Arguments());
	}

	@Override
	public IModel<Sample, Double> train(final double[][] features, final double[] expected,
			final String[] featureNames) {
		final OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.newSampleData(expected, features);
		return new LinearRegressionModel(regression.estimateRegressionParameters(), featureNames);
	}

	public static double[][] addIntercept(final double[][] x) {
		final int nVars = x[0].length;
		final double[][] xAug = new double[x.length][nVars + 1];
		for (int i = 0; i < x.length; ++i) {
			if (x[i].length != nVars) {
				throw new DimensionMismatchException(x[i].length, nVars);
			}
			xAug[i][0] = 1.0d;
			System.arraycopy(x[i], 0, xAug[i], 1, nVars);
		}
		return xAug;
	}

	public static Array2DRowRealMatrix addIntercept2(final double[][] x) {
		return new Array2DRowRealMatrix(addIntercept(x), false);
	}

}
