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

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.models.ILearnerPipeline;
import com.insightml.utils.jobs.IClient;

@Value.Immutable
public interface SimulationSetup<I extends Sample, E, P> {

	enum PERFORMANCE_SELECTOR {
		MEANDIAN, MEAN, MEDIAN, WORST, BEST
	}

	@Value.Default
	default boolean doDump() {
		return false;
	}

	@Value.Default
	default boolean doReport() {
		return true;
	}

	IClient getClient();

	FeaturesConfig<I, P> getConfig();

	default PERFORMANCE_SELECTOR getCriteria() {
		return PERFORMANCE_SELECTOR.MEAN;
	}

	@Nullable
	String getDatasetName();

	@Nullable
	EvaluationSlicesProvider<I> getEvaluationSlicesProvider();

	@Nullable
	Integer getLabelIndex();

	ILearnerPipeline<I, P>[] getLearner();

	ObjectiveFunction<? super E, ? super P>[] getObjectives();

}
