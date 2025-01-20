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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.simulation.ISimulation;
import com.insightml.evaluation.simulation.ImmutableSimulationSetup;
import com.insightml.evaluation.simulation.SimulationSetup;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerArguments.Argument;
import com.insightml.models.LearnerPipeline;
import com.insightml.utils.Arguments;
import com.insightml.utils.jobs.IClient;

public final class ParameterOptimizer<I extends Sample, E, P> {
	private static final Logger LOG = LoggerFactory.getLogger(ParameterOptimizer.class);

	private final ISimulation<I> simulation;
	private final ObjectiveFunction<? super E, ? super P> objective;
	private final IClient client;

	public ParameterOptimizer(final ISimulation<I> simulation, final ObjectiveFunction<? super E, ? super P> objective,
			final IClient client) {
		this.simulation = simulation;
		this.objective = objective;
		this.client = client;
	}

	public Arguments run(final ILearner<I, E, P> learner, final Iterable<I> train,
			final FeaturesConfig<I, P> featuresConfig) {
		return run(learner, learner.arguments(), train, featuresConfig);
	}

	public Arguments run(final ILearner<I, E, P> learner, final LearnerArguments parameters, final Iterable<I> train,
			final FeaturesConfig<I, P> featuresConfig) {
		final double[] params = new double[parameters.size()];
		final double[] stepSize = new double[params.length];
		int i = -1;
		final Arguments ar = (Arguments) learner.getOriginalArguments();
		for (final Argument arg : parameters) {
			params[++i] = ar.toDouble(arg.getName(), arg.getDefault());
			stepSize[i] = arg.getParameterSearchStepSize();
		}
		double best = simulate(learner, train, featuresConfig);
		LOG.info("Baseline: " + best + " for " + ar);
		while (true) {
			i = -1;
			int foundNewBest = i;
			for (final Argument arg : parameters) {
				++i;
				if (stepSize[i] == 0) {
					continue;
				}
				double val = params[i] - stepSize[i];
				final double left = val >= arg.getMin() ? run(learner, ar, arg, val, train, featuresConfig) : -999;
				if (left > -999) {
					log(left, ar, left > best);
				}
				val = params[i] + stepSize[i];
				final double right = val <= arg.getMax() ? run(learner, ar, arg, val, train, featuresConfig) : -999;
				if (right > -999) {
					log(right, ar, right > best);
				}
				double score = Math.max(left, right);

				val = left > right ? params[i] - stepSize[i] : params[i] + stepSize[i];
				ar.set(arg.getName(), val, true);

				if (score > best) {
					best = score;
					foundNewBest = i;
					params[i] = val;
				}

				final double dir = left > right ? -1 : 1;
				int stepsSinceLastBest = 0;
				while (true) {
					val += dir * stepSize[i];
					if (val < arg.getMin() || val > arg.getMax()) {
						break;
					}
					score = run(learner, ar, arg, val, train, featuresConfig);
					log(score, ar, score > best);
					if (score > best) {
						best = score;
						params[i] = val;
						foundNewBest = i;
						stepsSinceLastBest = 0;
					} else if (++stepsSinceLastBest > 5) {
						break;
					}
				}
				ar.set(arg.getName(), params[i], true);
			}
			// no need to do another iteration
			if (foundNewBest <= 0) {
				break;
			}
		}
		return ar;
	}

	private double simulate(final ILearner<I, E, P> learner, final Iterable<I> train,
			final FeaturesConfig<I, P> featuresConfig) {
		final SimulationSetup<I, E, P> setup = ImmutableSimulationSetup.<I, E, P> builder().config(featuresConfig)
				.learner(new LearnerPipeline(learner, true)).client(client).objectives(objective).doReport(false)
				.build();
		return simulation.run(train, setup)[0].getNormalizedResult();
	}

	private double run(final ILearner<I, E, P> learner, final Arguments args, final Argument arg, final double value,
			final Iterable<I> train, final FeaturesConfig<I, P> featuresConfig) {
		args.set(arg.getName(), arg.validate(value), true);
		return simulate(learner, train, featuresConfig);
	}

	private static void log(final double score, final Arguments ar, final boolean isNewBest) {
		LOG.info("Found new result: {} using {}{}", score, ar, isNewBest ? " - new best" : "");
	}
}
