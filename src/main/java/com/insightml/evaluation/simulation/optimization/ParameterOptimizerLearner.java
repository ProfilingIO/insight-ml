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
package com.insightml.evaluation.simulation.optimization;

import java.io.Serializable;
import java.util.Map.Entry;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.simulation.ISimulation;
import com.insightml.models.AbstractLearner;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Arguments;
import com.insightml.utils.jobs.IClient;

public final class ParameterOptimizerLearner<I extends Sample, E, P> extends AbstractLearner<I, E, P> {
	private final ILearner<I, E, P> learner;
	private final ISimulation<I> simulation;
	private final ObjectiveFunction<? super E, ? super P> objective;
	private final IClient client;

	public ParameterOptimizerLearner(final ILearner<I, E, P> learner, final ISimulation<I> simulation,
			final ObjectiveFunction<? super E, ? super P> objective, final IClient client) {
		super(new Arguments());
		this.learner = learner;
		this.simulation = simulation;
		this.objective = objective;
		this.client = client;
	}

	@Override
	public IModel<I, P> run(final LearnerInput<? extends I, ? extends E> input) {
		final Arguments args = (Arguments) learner.getOriginalArguments();

		final Arguments best = new ParameterOptimizer<I, E, P>(simulation, objective, client)
				.run(learner, (Iterable<I>) input.getTrain(), (FeaturesConfig<I, P>) input.config);

		for (final Entry<String, Serializable> arg : best.entrySet()) {
			args.set(arg.getKey(), arg.getValue(), true);
		}

		return learner.run(input);
	}

}
