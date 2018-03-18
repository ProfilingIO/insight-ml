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
package com.insightml.models.optimization;

import org.apache.commons.math3.util.Pair;
import org.junit.Ignore;

import com.insightml.AbstractModelTest;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.LogLoss;
import com.insightml.evaluation.functions.RMSE;
import com.insightml.math.distributions.IDiscreteDistribution;
import com.insightml.models.ILearner;
import com.insightml.models.regression.OLS;

@Ignore
public final class LinearModelBlenderTest extends AbstractModelTest {

	@Override
	protected Pair<? extends ILearner<Sample, ? super Double, Double>, Double> getNumeric() {
		return new Pair(new LinearModelBlender(true, new double[1][2], new RMSE(), new OLS()), -0.75341);
	}

	@Override
	protected Pair getBoolean() {
		return new Pair<>(new LinearModelBlender(true, new double[1][2], new LogLoss(false), new OLS()), -0.03123);
	}

	@Override
	protected Pair<? extends ILearner<Sample, String, IDiscreteDistribution<String>>, Double> getNominal() {
		// TODO Auto-generated method stub
		return null;
	}
}
