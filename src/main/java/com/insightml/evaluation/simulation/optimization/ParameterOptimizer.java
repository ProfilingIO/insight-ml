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

import com.insightml.data.IDataset;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.simulation.ISimulation;
import com.insightml.evaluation.simulation.SimulationSetupImpl;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerArguments.Argument;
import com.insightml.models.LearnerPipeline;
import com.insightml.utils.Arguments;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.jobs.IClient;

public final class ParameterOptimizer<I extends Sample, E, P> {
	private static final Logger LOG = LoggerFactory.getLogger(ParameterOptimizer.class);

	private final ISimulation<I> simulation;
	private final ObjectiveFunction<E, P> objective;
	private final IClient client;

	public ParameterOptimizer(final ISimulation<I> simulation, final ObjectiveFunction<E, P> objective,
			final IClient client) {
		this.simulation = simulation;
		this.objective = objective;
		this.client = client;
	}

	public void run(final ILearner<Sample, Object, Object> learner, final IDataset<I, P> dataset,
			final IArguments args) {
		final Iterable<I> train = dataset.loadTraining(null);
		final LearnerArguments argz = learner.arguments();
		final double[] params = new double[argz.size()];
		final double[] stepSize = new double[params.length];
		int i = -1;
		final Arguments ar = ((Arguments) args).copy();
		for (final Argument arg : argz) {
			params[++i] = args.toDouble(arg.getName(), arg.getDefault());
			final double min = arg.getMin();
			final double max = arg.getMax();
			stepSize[i] = Check.num((max - min) * 1.0 / 15, 0.000001, 99);
			if (max > 1 && max % 1 == 0 && min % 1 == 0) {
				stepSize[i] = Math.max(1, Math.round(stepSize[i]));
			}
			ar.set(arg.getName(), params[i]);
		}
		double best = run(learner, ar, argz.iterator().next(), params[0], train, dataset);
		LOG.info("Baseline: " + best);
		while (true) {
			i = -1;
			double newBest = best;
			double last = best;
			for (final Argument arg : argz) {
				double val = params[++i] - stepSize[i];
				final double left = val >= arg.getMin() ? Math.max(newBest, run(learner, ar, arg, val, train, dataset))
						: -999;
				val = params[i] + stepSize[i];
				final double right = val <= arg.getMax() ? Math.max(newBest, run(learner, ar, arg, val, train, dataset))
						: -999;
				double score = Math.max(left, right);
				if (score > last) {
					if (score > newBest) {
						newBest = score;
						log(newBest, ar);
					}
					params[i] = left > right ? params[i] - stepSize[i] : val;
					final double dir = left > right ? -1 : 1;
					last = score;
					while (true) {
						val += dir * stepSize[i];
						if (val < arg.getMin() || val > arg.getMax()) {
							break;
						}
						score = run(learner, ar, arg, val, train, dataset);
						if (score > newBest) {
							newBest = score;
							log(newBest, ar);
						} else if (score <= last) {
							break;
						}
						params[i] = val;
					}
				}
				ar.set(arg.getName(), params[i]);
				last = score;
			}
			if (newBest <= best) {
				break;
			}
			best = newBest;
			log(newBest, ar);
		}
	}

	private double run(final ILearner<Sample, Object, Object> learner, final Arguments args, final Argument arg,
			final double value, final Iterable<I> train, final IDataset<I, P> dataset) {
		args.set(arg.getName(), arg.validate(value));
		final SimulationSetupImpl setup = new SimulationSetupImpl<>(dataset.getName(), dataset.getFeaturesConfig(args),
				null, new LearnerPipeline[] { new LearnerPipeline(learner, true), }, client, true,
				new ObjectiveFunction[] { objective });
		return simulation.run(train, setup)[0].getNormalizedResult();
	}

	private static void log(final double newBest, final Arguments ar) {
		LOG.info("Found new best: " + newBest + " using " + ar);
	}
}
