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

import com.insightml.data.IDataset;
import com.insightml.data.samples.AnonymousSample;
import com.insightml.data.samples.ISample;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.evaluation.simulation.CrossValidation;
import com.insightml.evaluation.simulation.SimulationResults;
import com.insightml.evaluation.simulation.SimulationSetup;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerPipeline;
import com.insightml.utils.Arguments;
import com.insightml.utils.jobs.ThreadedClient;

public final class Tests {

	private Tests() {
	}

	public static <I extends ISample, E, P> SimulationSetup<I, E, P> simulationSetup(final IDataset<I, E, P> dataset,
			final LearnerPipeline<? super I, E, P> learner, final IObjectiveFunction<? super E, ? super P>... metrics) {
		return new SimulationSetup<>(dataset.getName(), dataset.getFeaturesConfig(null), null,
				new LearnerPipeline[] { learner }, new ThreadedClient(), false, metrics);
	}

	public static <I extends ISample> CrossValidation<I> getCv() {
		return new CrossValidation<>(5, 1, null);
	}

	public static <I extends ISample, E, P> SimulationResults<I, E, P> cv(final IDataset<I, E, P> instances,
			final LearnerPipeline learner, final IObjectiveFunction<E, P>[] objective) {
		return new CrossValidation<I>(5, 1, null).run(instances.loadTraining(null),
				simulationSetup(instances, learner, objective))[0];
	}

	public static Arguments arguments() {
		return new Arguments("fs", "forward", "ps", "mean", "lt", "none", "fsb", "1", "fst", "0.001", "cvf", "10",
				"cvr", "false", "epochs", "10", "rank", "RANKBOOST", "score", "P");
	}

	public static <I extends AnonymousSample, E extends Serializable> double testLearner2(final ILearner learner,
			final TestData testData, final IObjectiveFunction<? super I, ? super E> objective, final Double expected) {
		IDataset instances;
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

	public static <I extends AnonymousSample, E extends Serializable> double testLearner(final ILearner learner,
			final IDataset instances, final IObjectiveFunction<? super I, ? super E> objective, final Double expected) {
		return testLearner(new LearnerPipeline<>(learner, 1.0), instances, objective, expected);
	}

	public static <I extends AnonymousSample, E extends Serializable> double testLearner(final LearnerPipeline learner,
			final IDataset instances, final IObjectiveFunction<? super I, ? super E> objective, final Double expected) {
		final double result = cv(instances, learner, new IObjectiveFunction[] { objective }).getNormalizedResult();
		if (expected != null) {
			Assert.assertEquals(expected, result, 0.000005);
		}
		return result;
	}

	public enum TestData {
		BOOLEAN, NUMERIC
	}

}
