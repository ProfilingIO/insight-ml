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

import com.google.common.base.Preconditions;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Check;

public abstract class AbstractModelBlender<I extends Sample, E>
		extends AbstractParameterLearner<I, E, IModel<I, Double>[], Double[]> {

	private final ILearner<I, ? super E, ? super Double>[] learner;

	protected AbstractModelBlender(final ILearner<I, ? super E, ? super Double>[] learner, final double[][] init,
			final ObjectiveFunction<? super E, ? super Double> obj) {
		super(init, obj);
		this.learner = Check.size(learner, 1, 20);
	}

	@Override
	public final IModel<I, Double>[] train(final ISamples<I, E> train, final int labelIndex) {
		final IModel<I, Double>[] models = new IModel[learner.length];
		for (int i = 0; i < learner.length; ++i) {
			models[i] = learner[i].run(new LearnerInput(train, null, null, labelIndex));
		}
		return models;
	}

	@Override
	protected final Double[][] computeCachable(final ISamples<? extends I, ?> instances,
			final IModel<I, Double>[] models) {
		final Double[][] map = new Double[instances.size()][models.length];
		for (int i = 0; i < models.length; ++i) {
			final Double[] preds = models[i].apply(instances);
			for (int j = 0; j < preds.length; ++j) {
				map[j][i] = Preconditions.checkNotNull(preds[j]);
			}
		}
		return map;
	}

	@Override
	public String toString() {
		return "{" + getInitial()[0].length + "}" + learner[0].getName();
	}
}
