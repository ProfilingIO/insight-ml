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

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.math.optimization.Optimizable;
import com.insightml.models.AbstractLearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerInput;
import com.insightml.utils.types.Triple;

abstract class AbstractParameterLearner<I extends Sample, E, T, C> extends AbstractLearner<I, E, Double> {
	private static final long serialVersionUID = 5305316932807128370L;

	private final double[][] initial;
	private final ObjectiveFunction<? super E, ? super Double> objective;

	protected AbstractParameterLearner(final double[][] init, final ObjectiveFunction<? super E, ? super Double> obj) {
		super(null);
		this.initial = init;
		this.objective = obj;
	}

	@Override
	public final IModel<I, Double> run(final LearnerInput<? extends I, ? extends E> input) {
		final T train = train((ISamples<I, E>) input.getTrain(), input.labelIndex);
		final int index = initial.length > 1 ? input.labelIndex : 0;
		final ParameterModel<I, T, C> model = new ParameterModel<>(train, initial[index], input.labelIndex, this);
		final Optimizable trainObjective = objective(model, (ISamples<I, E>) input.getTrain(), train, input.labelIndex);
		final Optimizable testObjective = input.valid != null
				? objective(model, (ISamples<I, E>) input.valid, train, input.labelIndex)
				: null;
		final Triple<double[], Double, Double> params = (testObjective != null ? testObjective : trainObjective)
				.max(testObjective != null ? trainObjective : testObjective, initial[index]);
		model.params = params.getFirst();
		return model;
	}

	private Optimizable objective(final ParameterModel<I, T, C> model, final ISamples<I, E> samples, final T train,
			final int labelIndex) {
		final C[] cachable = computeCachable(samples, train);
		return new ObjectiveFunc<>(model, samples, cachable, initial, objective, labelIndex);
	}

	public abstract T train(ISamples<I, E> train, int labelIndex);

	protected abstract C[] computeCachable(ISamples<? extends I, ?> instances, T train);

	public abstract double predict(double[] params, C cached, int labelIndex);

	final double[][] getInitial() {
		return initial;
	}

}
