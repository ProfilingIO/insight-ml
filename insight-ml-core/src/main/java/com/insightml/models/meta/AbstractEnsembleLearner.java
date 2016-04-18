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

import java.util.Random;

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.models.AbstractLearner;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Utils;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.Pair;

public abstract class AbstractEnsembleLearner<S extends ISample, E, O> extends AbstractLearner<S, E, O> {

	private final ILearner<S, ? extends E, O>[] learners;

	protected AbstractEnsembleLearner(final IArguments arguments, final ILearner<S, ? extends E, O>[] learners) {
		super(arguments);
		this.learners = learners.clone();
	}

	@Override
	public final IModel<S, O> run(final LearnerInput<? extends S, ? extends E, ?> input) {
		Check.num(input.getTrain().size(), 50, 9999999);
		return createModel((ISamples<S, E>) input.getTrain(), learners, input.labelIndex);
	}

	protected final ILearner<S, ? extends E, O>[] getLearners() {
		return learners;
	}

	protected abstract IModel<S, O> createModel(ISamples<S, E> instances, ILearner<S, ? extends E, O>[] learner,
			int labelIndex);

	Pair<SamplesMapping<ISample, Object>, double[]> sampleError(final ISamples<ISample, Object> instances,
			final double[] preds, final Object[] expected, final Random random) {
		final SamplesMapping<ISample, Object> sample = (SamplesMapping<ISample, Object>) instances
				.sample(argument("bag"), random).getFirst();
		final int[] map = sample.getIndexMap();
		final double[] labels = new double[map.length];
		for (int i = 0; i < map.length; ++i) {
			final int idx = map[i];
			labels[i] = Utils.toDouble(expected[idx]) - preds[idx];
		}
		return new Pair<>(sample, labels);
	}

}
