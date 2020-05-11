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

import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.models.ILearner;
import com.insightml.utils.Check;

public final class LinearModelBlender<I extends Sample, E> extends AbstractModelBlender<I, E>
		implements IModelBlender<I, E, Double> {
	private static final long serialVersionUID = 712163607650755823L;

	private final boolean useOffset;

	public LinearModelBlender(final boolean useOffset, final double[][] init,
			final ObjectiveFunction<? super E, ? super Double> obj, final ILearner<I, E, ? super Double>... learner) {
		super(learner, init, obj);
		this.useOffset = useOffset;
	}

	@Override
	public double predict(final double[] params, final Double[] predictions, final int labelIndex) {
		final int numModels = predictions.length;
		Check.argument(params.length >= 2);
		Check.argument(params.length == Math.max(2, numModels + (useOffset ? 0 : -1)));
		double[] params2;
		if (numModels == 1) {
			params2 = params;
		} else {
			params2 = new double[numModels + (useOffset ? 1 : 0)];
			double sum = 0;
			for (int i = 0; i < numModels - 1; ++i) {
				params2[i] = params[i];
				sum += params[i];
			}
			params2[numModels - 1] = 1 - sum;
			if (useOffset) {
				params2[numModels] = params[numModels - 1];
			}
		}
		double result = !useOffset ? 0 : params2[numModels];
		for (int model = 0; model < numModels; ++model) {
			result += predictions[model] * params2[model];
		}
		return result;
	}

	@Override
	public String getName() {
		return "Linear" + super.toString();
	}

}
