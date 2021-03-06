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
package com.insightml.evaluation.functions;

import com.insightml.data.samples.Sample;

public final class RMSLE extends AbstractObjectiveFunctionFrame<Object, Object> {
	private static final long serialVersionUID = -6151302549655675505L;

	@Override
	public double instance(final Object prediction, final Object label, final Sample sample, final int labelIndex) {
		final double[] predAndAct = toDouble(prediction, label);
		final double logPred = predAndAct[0] <= -1 ? 0 : log(predAndAct[0]);
		final double logAct = log(predAndAct[1]);
		return Math.pow(logPred - logAct, 2);
	}

	@Override
	protected double getResult(final double sum, final double weightSum) {
		return Math.sqrt(sum / weightSum);
	}

	private static double log(final double value) {
		return Math.log1p(value);
	}

	@Override
	public double normalize(final double score) {
		return -score;
	}

}
