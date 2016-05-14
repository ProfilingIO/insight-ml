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
package com.insightml.evaluation.simulation;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.insightml.data.IDataset;
import com.insightml.data.samples.ISample;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.models.ILearner;
import com.insightml.models.ILearnerPipeline;
import com.insightml.models.IModelTask;
import com.insightml.models.LearnerPipeline;
import com.insightml.models.ModelPipeline;
import com.insightml.models.Predictions;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.jobs.IJobBatch;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.ui.reports.ImportanceReport;
import com.insightml.utils.ui.reports.SimulationResultsDumper;

public abstract class AbstractSimulation<I extends ISample> extends AbstractModule implements ISimulation<I> {
	private static final long serialVersionUID = -2573735409140672897L;

	private final Logger logger = LoggerFactory.getLogger(AbstractSimulation.class);

	private final SimulationResultConsumer simulationResultConsumer;

	protected AbstractSimulation(final String name, final SimulationResultConsumer simulationResultConsumer) {
		super(name);
		this.simulationResultConsumer = simulationResultConsumer;
	}

	@Override
	public final <E, P> ISimulationResults<E, P> simulate(final IDataset<I, E, P> dataset, final IArguments arguments,
			final double[][] blendingParams, final boolean delayInit, final boolean report,
			final IModelTask<I, E, P> task) {
		final ILearner<ISample, Object, Object> learnerr = task.getLearner(arguments, blendingParams);
		final ISimulationResults<E, P> result = run(
				new ILearnerPipeline[] { new LearnerPipeline<>(learnerr, 1.0, !delayInit), }, dataset, arguments,
				report, task)[0];
		dataset.close();
		return result;
	}

	@Override
	public <E, P> ISimulationResults<E, P>[] run(final ILearnerPipeline<I, P>[] learner,
			final IDataset<I, E, P> dataset, final IArguments arguments, final boolean report,
			final IModelTask<I, E, P> task) {
		return run(Preconditions.checkNotNull(dataset.loadTraining(null)),
				task.getSimulationSetup(learner, dataset, arguments, report, null));
	}

	protected final <E, P> ISimulationResults<E, P>[] run(final Iterable<I> train, final Iterable<I> test,
			final ISimulationSetup<I, E, P> setup) {
		final ILearnerPipeline<I, P>[] learners = setup.getLearner();
		final SimulationResults<I, E, P>[] results = new SimulationResults[learners.length];
		final int numLabels = true ? 1 : train.iterator().next().getExpected().length;
		for (int l = 0; l < learners.length; ++l) {
			results[l] = new SimulationResults<>(1, numLabels, setup);
			for (int i = 0; i < numLabels; ++i) {
				logger.debug("Training model...");
				final ModelPipeline<I, P> model = learners[l].run(train, test, setup.getConfig(), i);
				if (setup.doReport()) {
					logger.info(model.info());
					if (false) {
						ImportanceReport.writeLatex(model, setup.getDatasetName(), "logs/models/");
					}
				}
				logger.debug("Making predictions...");
				results[l].add(new Predictions<E, P>(1, model, test));
			}
			notify(learners[l].getName(), results[l], setup, null);
		}
		return results;
	}

	public <E, P> SimulationResults<I, E, P>[] makeResults(final IJobBatch<Predictions<E, P>[]> batch,
			final ISimulationSetup<I, E, P> setup) {
		final ILearnerPipeline<I, P>[] learner = setup.getLearner();
		final SimulationResults<I, E, P>[] result = new SimulationResults[learner.length];
		for (int i = 0; i < learner.length; ++i) {
			result[i] = new SimulationResults<>(batch.size(), 1, setup);
		}
		for (final Predictions<E, P>[] preds : batch.run()) {
			for (int i = 0; i < preds.length; ++i) {
				result[i].add(preds[i]);
			}
		}
		return result;
	}

	protected final <E, P> void notify(final String learn, final ISimulationResults<E, P> performance,
			final ISimulationSetup<I, E, P> setup, final String subfolder) {
		Check.length(learn, 2, 250);
		simulationResultConsumer.accept(this, learn, performance, setup);
		if (setup.doDump()) {
			dump(subfolder, setup.getDatasetName(), learn, (SimulationResults<I, E, P>) performance);
		}
	}

	private <E, P> void dump(final String subfolder, final String dataset, final String learner,
			final SimulationResults<I, E, P> performance) {
		final String filename = learner.substring(0, Math.min(200, learner.length())) + "_" + dataset + ".csv";
		final String folder = "logs/" + getClass().getSimpleName() + "/" + (subfolder == null ? "" : subfolder + "/");
		new File(folder).mkdirs();
		final IObjectiveFunction<? super E, ? super P>[] metrics = performance.getObjectives();
		SimulationResultsDumper.dump(folder + filename, performance.getPredictions(), metrics[0]);
	}

}
