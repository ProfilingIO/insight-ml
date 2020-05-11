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

import org.apache.commons.math3.util.Pair;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.models.AbstractLearner;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerInput;
import com.insightml.utils.IArguments;
import com.insightml.utils.Utils;

public abstract class AbstractEnsembleLearner<S extends Sample, E, O> extends AbstractLearner<S, E, O> {

	private ILearner<S, E, O>[] learners;

	AbstractEnsembleLearner() {
	}

	protected AbstractEnsembleLearner(final IArguments arguments, final ILearner<S, E, O>[] learners) {
		super(arguments);
		this.learners = learners.clone();
	}

	protected final ILearner<S, E, O>[] getLearners() {
		return learners;
	}

	@Override
	public IModel<S, O> run(final LearnerInput<? extends S, ? extends E> input) {
		throw new UnsupportedOperationException(getClass().getName());
	}

	@Override
	public abstract IModel<S, O> run(final ISamples<? extends S, ? extends E> samples,
			final ISamples<? extends S, ? extends E> valid, final FeaturesConfig<? extends S, ?> config,
			final int labelIndex);

	Pair<SamplesMapping<S, E>, double[]> sampleError(final ISamples<S, E> instances, final double[] preds,
			final Object[] expected, final Random random) {
		final SamplesMapping<S, E> sample = (SamplesMapping<S, E>) instances.sample(argument("bag"), random).getFirst();
		final int[] map = sample.getIndexMap();
		final double[] labels = new double[map.length];
		for (int i = 0; i < map.length; ++i) {
			final int idx = map[i];
			labels[i] = Utils.toDouble(expected[idx]) - preds[idx];
		}
		return new Pair<>(sample, labels);
	}

}
