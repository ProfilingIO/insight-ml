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
package com.insightml.models.trees;

import org.junit.Test;

import com.insightml.TestDatasets;
import com.insightml.data.samples.SimpleSample;
import com.insightml.evaluation.functions.RMSE;
import com.insightml.models.ModelBenchmark;
import com.insightml.models.meta.GBRT;
import com.insightml.models.meta.RandomForest;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.models.regression.OLS;

public final class RegTreeLearnerTest {

	@Test
	public void test() {
		final ModelBenchmark<SimpleSample, Double> benchmark = new ModelBenchmark<>(TestDatasets.createNumeric(),
				new RMSE());
		benchmark.addLearner(new OLS(), -0.75341);
		benchmark.addLearner(new RegTree(7, 12), -0.72892);
		benchmark.addLearner(new RandomForest(120, 7, 7, 4, 0.55, 0.9, VoteStrategy.HARMONIC), -0.68449);
		if (false) {
			benchmark.addLearner(new GBRT(500, 0.06, 0.6, 12, 12, 6), -0.58897);
		}
		benchmark.run();
	}
}
