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
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.insightml.data.IDataset;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.models.ILearner;
import com.insightml.models.ILearnerPipeline;
import com.insightml.models.IModelTask;
import com.insightml.models.LearnerPipeline;
import com.insightml.models.ModelPipeline;
import com.insightml.models.Predictions;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.io.serialization.ISerializer;
import com.insightml.utils.jobs.IJobBatch;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.ui.reports.SimulationResultsDumper;

public abstract class AbstractSimulation<I extends Sample> extends AbstractModule implements ISimulation<I> {
	private static final long serialVersionUID = -2573735409140672897L;

	private final Logger logger = LoggerFactory.getLogger(AbstractSimulation.class);

	private final SimulationResultConsumer simulationResultConsumer;
	private final ISerializer serializer;

	protected AbstractSimulation(final String name, final SimulationResultConsumer simulationResultConsumer,
			final ISerializer serializer) {
		super(name);
		this.simulationResultConsumer = simulationResultConsumer;
		this.serializer = serializer;
	}

	@Override
	public final <E, P> ISimulationResults<E, P> simulate(final IDataset<I, P> dataset, final IArguments arguments,
			final double[][] blendingParams, final boolean delayInit, final boolean report,
			final IModelTask<I, E, P> task) {
		final ILearner<Sample, Object, Object> learnerr = task.getLearner(arguments, blendingParams);
		final ISimulationResults<E, P> result = run(
				new ILearnerPipeline[] { new LearnerPipeline<>(learnerr, 1.0, !delayInit, serializer), },
				dataset,
				arguments,
				report,
				task)[0];
		dataset.close();
		return result;
	}

	@Override
	public <E, P> ISimulationResults<E, P>[] run(final ILearnerPipeline<I, P>[] learner, final IDataset<I, P> dataset,
			final IArguments arguments, final boolean report, final IModelTask<I, E, P> task) {
		return run(Preconditions.checkNotNull(dataset.loadTraining(null)),
				task.getSimulationSetup(learner, dataset, arguments, report, null));
	}

	public final <E, P> ISimulationResults<E, P>[] run(final Supplier<? extends Iterable<I>> train,
			final Supplier<? extends Iterable<I>> test, final SimulationSetup<I, E, P> setup) {
		final ILearnerPipeline<I, P>[] learners = setup.getLearner();
		final SimulationResults<E, P>[] results = new SimulationResults[learners.length];
		final int numLabels = 1;
		for (int l = 0; l < learners.length; ++l) {
			final SimulationResultsBuilder<E, P> builder = new SimulationResultsBuilder<>(learners[l].getName(), 1,
					numLabels, setup);
			for (int i = 0; i < numLabels; ++i) {
				logger.debug("Training model...");
				final long start = System.currentTimeMillis();
				final ModelPipeline<I, P> model = learners[l].run(train.get(), null, setup.getConfig(), i);
				logger.debug("Making predictions...");
				builder.add(Predictions.create(1, model, test.get(), (int) (System.currentTimeMillis() - start)));
				if (setup.doReport()) {
					logger.info(model.info());
				}
			}
			results[l] = builder.build();
			notify(results[l], setup, null);
		}
		return results;
	}

	public <E, P> SimulationResults<E, P>[] makeResults(final IJobBatch<Predictions<E, P>[]> batch,
			final SimulationSetup<I, E, P> setup) {
		final ILearnerPipeline<I, P>[] learner = setup.getLearner();
		final SimulationResultsBuilder<E, P>[] builders = new SimulationResultsBuilder[learner.length];
		for (int i = 0; i < learner.length; ++i) {
			builders[i] = new SimulationResultsBuilder<>(learner[i].getName(), batch.size(), 1, setup);
		}
		for (final Predictions<E, P>[] preds : batch.run()) {
			for (int i = 0; i < preds.length; ++i) {
				builders[i].add(preds[i]);
			}
		}
		final SimulationResults<E, P>[] result = new SimulationResults[builders.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = builders[i].build();
		}
		return result;
	}

	protected final <E, P> void notify(final ISimulationResults<E, P> performance, final SimulationSetup<I, E, P> setup,
			final String subfolder) {
		final String learn = performance.getModelName();
		Check.length(learn, 2, 250);
		if (simulationResultConsumer != null) {
			simulationResultConsumer.accept(this, learn, performance, setup);
		}
		if (setup.doDump()) {
			dump(subfolder, setup.getDatasetName(), learn, (SimulationResults<E, P>) performance);
		}
	}

	private <E, P> void dump(final String subfolder, final String dataset, final String learner,
			final SimulationResults<E, P> performance) {
		final String filename = learner.substring(0, Math.min(200, learner.length())) + "_" + dataset + ".csv";
		final String folder = "logs/" + getClass().getSimpleName() + "/" + (subfolder == null ? "" : subfolder + "/");
		new File(folder).mkdirs();
		final ObjectiveFunction<? super E, ? super P>[] metrics = performance.getObjectives();
		SimulationResultsDumper.dump(folder + filename, performance.getPredictions(), metrics[0]);
	}

}
