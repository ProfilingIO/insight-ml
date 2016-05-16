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

import java.io.Serializable;

import org.apache.commons.math3.util.Pair;

import com.insightml.Tests;
import com.insightml.data.IDataset;
import com.insightml.data.samples.SimpleSample;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.utils.types.collections.PairList;

public final class ModelBenchmark<I extends SimpleSample, E extends Serializable> {

	private final IDataset instances;
	private final ObjectiveFunction objective;
	private final PairList<ILearner, Double> learners = new PairList<>(true);

	public ModelBenchmark(final IDataset<I, ?, ?> instances, final ObjectiveFunction<? super I, ? super E> objective) {
		this.instances = instances;
		this.objective = objective;
	}

	public void addLearner(final ILearner<? super I, ? super E, ?> learner, final double expected) {
		learners.add(learner, expected);
	}

	public void run() {
		for (final Pair<ILearner, Double> learner : learners) {
			Tests.testLearner(learner.getFirst(), instances, objective, learner.getSecond());
		}
	}
}
