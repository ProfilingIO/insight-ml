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
package com.insightml;

import java.io.Serializable;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.IDataset;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.SimpleSample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.simulation.BasicSimulationResult;
import com.insightml.evaluation.simulation.CrossValidation;
import com.insightml.evaluation.simulation.ImmutableSimulationSetup;
import com.insightml.evaluation.simulation.SimulationResultConsumer;
import com.insightml.evaluation.simulation.SimulationResults;
import com.insightml.evaluation.simulation.SimulationSetup;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerPipeline;
import com.insightml.utils.Arguments;
import com.insightml.utils.jobs.ThreadedClient;

public final class Tests {
	private static final Logger LOG = LoggerFactory.getLogger(Tests.class);

	public enum TestData {
		BOOLEAN, NUMERIC
	}

	private Tests() {
	}

	public static <I extends Sample> CrossValidation<I> getCv() {
		return new CrossValidation<>(5, 1, null);
	}

	public static Arguments arguments() {
		return new Arguments("fs", "forward", "ps", "mean", "lt", "none", "fsb", "1", "fst", "0.001", "cvf", "10",
				"cvr", "false", "epochs", "10", "rank", "RANKBOOST", "score", "P");
	}

	public static <I extends SimpleSample, E extends Serializable> double testLearner2(final ILearner learner,
			final TestData testData, final ObjectiveFunction<? super I, ? super E> objective, final Double expected) {
		final IDataset instances;
		switch (testData) {
		case BOOLEAN:
			instances = TestDatasets.createBoolean();
			break;
		case NUMERIC:
			instances = TestDatasets.createNumeric();
			break;
		default:
			throw new IllegalArgumentException();
		}
		return testLearner(new LearnerPipeline<>(learner, 1.0), instances, objective, expected);
	}

	public static <I extends SimpleSample, E extends Serializable> double testLearner(final LearnerPipeline learner,
			final IDataset instances, final ObjectiveFunction<? super I, ? super E> objective, final Double expected) {
		final double result = cv(instances, learner, new ObjectiveFunction[] { objective }).getNormalizedResult();
		if (expected != null) {
			Assert.assertEquals(expected, result, 0.000005);
		}
		return result;
	}

	public static <I extends Sample, E, P> SimulationResults<E, P> cv(final IDataset<I, P> instances,
			final LearnerPipeline learner, final ObjectiveFunction<E, P>[] objective) {
		final SimulationResultConsumer resultConsumer = (simulation, learn, result, setup) -> LOG
				.info(BasicSimulationResult.of(learn, result).toString());
		return new CrossValidation<I>(5, 1, resultConsumer).run(instances.loadTraining(null),
				simulationSetup(instances, learner, objective))[0];
	}

	public static <I extends Sample, E, P> SimulationSetup<I, E, P> simulationSetup(final IDataset<I, P> dataset,
			final LearnerPipeline<? super I, E, P> learner, final ObjectiveFunction<? super E, ? super P>... metrics) {
		return ImmutableSimulationSetup.<I, E, P> builder().datasetName(dataset.getName())
				.config(dataset.getFeaturesConfig(null)).learner(new LearnerPipeline[] { learner })
				.client(new ThreadedClient()).objectives(metrics).doReport(false).build();
	}

	public static <I extends SimpleSample, E extends Serializable> double testLearner(final ILearner learner,
			final IDataset instances, final ObjectiveFunction<? super I, ? super E> objective, final Double expected) {
		return testLearner(new LearnerPipeline<>(learner, 1.0), instances, objective, expected);
	}

}
