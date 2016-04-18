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

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.Table;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.google.common.collect.ImmutableMap;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.utils.Check;
import com.insightml.utils.Maps;
import com.insightml.utils.io.IDatabase;
import com.insightml.utils.io.JPAUtils;
import com.insightml.utils.time.Dates;
import com.insightml.utils.types.AbstractClass;

@Entity
@Table(name = "SimulationResult")
public final class SimulationResult extends AbstractClass implements Comparable<SimulationResult> {

	@Id
	@GeneratedValue
	private int id;

	private String _data;
	@Lob
	private String _model;
	private String _simulation;

	public int n;
	public float mean;
	public float stddev;
	public float min;
	public float max;

	private int updated;

	@ElementCollection
	@CollectionTable(name = "SimulationResult_metrics")
	private final Map<String, Float> metrics = new TreeMap<>();
	@ElementCollection
	@CollectionTable(name = "SimulationResult_history")
	private final Map<Integer, Float> history = Maps.create(16);

	public SimulationResult() {
	}

	private SimulationResult(final String data, final String model, final String simulation) {
		_data = Check.length(data, 2, 250);
		_model = Check.length(model, 2, 250);
		_simulation = Check.length(simulation, 2, 250);
	}

	public static SimulationResult updateOrCreate(final String simulationName, final String dataset,
			final String learner, final ISimulationResults<?, ?> performance, final IDatabase db) {
		final String data = dataset + " (" + performance.numPredictions() + ")";
		SimulationResult result = null;
		final EntityManager transaction = db == null ? null : db.makeTransaction();
		if (transaction != null) {
			try {
				result = JPAUtils.find(SimulationResult.class,
						ImmutableMap.of("_data", data, "_model", learner, "_simulation", simulationName),
						transaction);
				if (result != null) {
					result.history.put(result.updated, result.mean);
				}
			} catch (final NoResultException e) {
			}
		}
		if (result == null) {
			result = new SimulationResult(data, learner, simulationName);
			if (transaction != null) {
				transaction.persist(result);
			}
		}
		final StatisticalSummary[] results = performance.getResults();
		result.n = (int) results[0].getN();
		result.mean = (float) results[0].getMean();
		result.min = (float) results[0].getMin();
		result.max = (float) results[0].getMax();
		result.stddev = (float) results[0].getStandardDeviation();
		result.updated = (int) (Dates.getCalendar().getTimeInMillis() / 1000);
		final IObjectiveFunction<?, ?>[] metrics = performance.getObjectives();
		result.metrics.clear();
		for (int i = 0; i < metrics.length; ++i) {
			result.metrics.put(metrics[i].getName(), (float) results[i].getMean());
		}
		if (transaction != null) {
			transaction.merge(result);
			db.commitTransaction(transaction);
		}
		return result;
	}

	public String getData() {
		return _data;
	}

	public String getModel() {
		return _model;
	}

	public String getSimulation() {
		return _simulation;
	}

	public Map<String, Float> getMetrics() {
		return metrics;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "SimulationResult{" + n + ", " + mean + ", " + max + ", " + stddev + ", " + metrics + "}";
	}

	@Override
	public int compareTo(final SimulationResult o) {
		final int comp = _model.compareTo(o._model);
		return comp == 0 ? -1 : comp;
	}

}
