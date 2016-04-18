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
package com.insightml.models;

import com.insightml.data.IDataset;
import com.insightml.data.samples.ISample;
import com.insightml.evaluation.simulation.ISimulationSetup;
import com.insightml.utils.IArguments;

public interface IModelTask<I extends ISample, E, P> {

	IDataset<I, E, P> dataset(IArguments arguments);

	ModelPipeline<I, P> buildModel(IArguments args);

	ISimulationSetup<I, E, P> getSimulationSetup(ILearnerPipeline<I, P>[] learner, IDataset<I, E, P> dataset,
			IArguments arguments, boolean report, Integer labelIndex);

	<J extends ISample, F, Q> ILearner<J, F, Q> getLearner(IArguments arguments, double[][] params);

}
