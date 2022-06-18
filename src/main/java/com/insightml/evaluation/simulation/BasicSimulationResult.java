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

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.google.common.base.MoreObjects;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.utils.types.AbstractClass;

public class BasicSimulationResult extends AbstractClass implements SimulationResult {
	private final String model;
	private final Map<String, Float> metrics;

	private BasicSimulationResult(final String model, final Map<String, Float> metrics) {
		this.model = model;
		this.metrics = metrics;
	}

	public static SimulationResult of(final String learner, final ISimulationResults<?, ?> performance) {
		final StatisticalSummary[] results = performance.getResults();
		final ObjectiveFunction<?, ?>[] metrics = performance.getObjectives();
		final Map<String, Float> metric = new TreeMap<>();
		for (int i = 0; i < metrics.length; ++i) {
			metric.put(metrics[i].getName(), (float) results[i].getMean());
		}
		return new BasicSimulationResult(learner, metric);
	}

	@Override
	public String getModel() {
		return model;
	}

	@Override
	public Map<String, Float> getMetrics() {
		return metrics;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("model", model).add("metrics", metrics).toString();
	}
}
