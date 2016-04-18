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

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISample;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.models.ILearnerPipeline;
import com.insightml.models.Predictions;
import com.insightml.utils.Check;
import com.insightml.utils.jobs.IClient;
import com.insightml.utils.jobs.IJobBatch;
import com.insightml.utils.types.AbstractClass;

public final class SimulationSetup<I extends ISample, E, P> extends AbstractClass implements ISimulationSetup<I, E, P> {

	private final String datasetName;
	private final FeaturesConfig<I, P> config;
	private final Integer labelIndex;
	private final ILearnerPipeline<I, P>[] learner;
	private final IObjectiveFunction<? super E, ? super P>[] objectives;
	private final boolean report;
	private final IClient client;

	public SimulationSetup(final String datasetName, final FeaturesConfig<I, P> config, final Integer labelIndex,
			final ILearnerPipeline<I, P>[] learner, final IClient client, final boolean report,
			final IObjectiveFunction<? super E, ? super P>[] objectives) {
		this.datasetName = datasetName;
		this.config = config;
		this.labelIndex = labelIndex;
		this.learner = learner;
		this.objectives = Check.size(objectives, 1, 14);
		this.report = report;
		this.client = Check.notNull(client);
	}

	public IJobBatch<Predictions<E, P>[]> createBatch() {
		return client.newBatch();
	}

	@Override
	public String getDatasetName() {
		return Check.notNull(datasetName);
	}

	@Override
	public FeaturesConfig<I, P> getConfig() {
		return config;
	}

	public Integer getLabelIndex() {
		return labelIndex;
	}

	@Override
	public ILearnerPipeline<I, P>[] getLearner() {
		return learner;
	}

	@Override
	public IObjectiveFunction<? super E, ? super P>[] getObjectives() {
		return objectives;
	}

	@Override
	public PERFORMANCE_SELECTOR getCriteria() {
		return PERFORMANCE_SELECTOR.MEAN;
	}

	@Override
	public boolean doReport() {
		return report;
	}

	@Override
	public boolean doDump() {
		return false && report;
	}

}
