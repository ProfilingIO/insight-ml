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

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.math.Vectors;
import com.insightml.math.optimization.AbstractOptimizable;

public final class ObjectiveFunc<I extends ISample, E, T, C> extends AbstractOptimizable {

	private final ISamples<I, E> instances;
	private final ParameterModel<I, T, C> model;
	private final C[] cachable;
	private final int labelIndex;
	private final IObjectiveFunction<? super E, ? super Double> objective;

	public ObjectiveFunc(final ParameterModel<I, T, C> model, final ISamples<I, E> instances, final C[] cachable,
			final double[][] initial, final IObjectiveFunction<? super E, ? super Double> objective,
			final int labelIndex) {
		super(10000, 0.000001, Vectors.fill(-10, initial[labelIndex].length), Vectors.fill(10,
				initial[labelIndex].length), null, true);
		this.instances = instances;
		this.model = model;
		this.cachable = cachable;
		this.labelIndex = labelIndex;
		this.objective = objective;
	}

	@Override
	public double value(final double[] point) {
		model.params = point;
		return objective.normalize(objective.label(model.run(instances, cachable),
				instances.expected(labelIndex),
				instances.weights(labelIndex),
				instances,
				labelIndex).getMean());
	}
}