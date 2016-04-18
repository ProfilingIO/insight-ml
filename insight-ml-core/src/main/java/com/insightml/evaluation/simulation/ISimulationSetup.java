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

public interface ISimulationSetup<I extends ISample, E, P> {

    public enum PERFORMANCE_SELECTOR {
        MEANDIAN, MEAN, MEDIAN, WORST, BEST;
    }

    ILearnerPipeline<I, P>[] getLearner();

    FeaturesConfig<I, P> getConfig();

    boolean doReport();

    String getDatasetName();

    boolean doDump();

    IObjectiveFunction<? super E, ? super P>[] getObjectives();

    PERFORMANCE_SELECTOR getCriteria();

}
