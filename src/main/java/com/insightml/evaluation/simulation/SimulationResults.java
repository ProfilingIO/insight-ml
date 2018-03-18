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

import java.io.Serializable;

import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.simulation.SimulationSetup.PERFORMANCE_SELECTOR;
import com.insightml.math.statistics.Stats;
import com.insightml.models.Predictions;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.collections.PairList;
import com.insightml.utils.ui.SimpleFormatter;
import com.insightml.utils.ui.UiUtils;

public final class SimulationResults<E, P> extends AbstractClass implements Serializable, ISimulationResults<E, P> {
	private static final long serialVersionUID = 2715905427176152958L;

	private final String learner;
	private final ObjectiveFunction<? super E, ? super P>[] objectives;
	private final PERFORMANCE_SELECTOR criteria;

	private final Predictions<E, P>[][] predictions;
	private final Stats[] stats;

	private final int trainingIimeInMillis;
	private final int predictionIimeInMillis;

	public SimulationResults(final String learner, final ObjectiveFunction<? super E, ? super P>[] objectives,
			final PERFORMANCE_SELECTOR criteria, final Predictions<E, P>[][] predictions, final Stats[] stats,
			final int trainingIimeInMillis, final int predictionIimeInMillis) {
		this.learner = learner;
		this.objectives = objectives;
		this.criteria = criteria;
		this.predictions = predictions;
		this.stats = stats;
		this.trainingIimeInMillis = trainingIimeInMillis;
		this.predictionIimeInMillis = predictionIimeInMillis;
	}

	@Override
	public String getModelName() {
		return learner;
	}

	@Override
	public int numPredictions() {
		int sum = 0;
		for (final Predictions<E, P>[] preds : predictions) {
			for (final Predictions<E, P> pred : preds) {
				sum += pred.size();
			}
		}
		return sum;
	}

	@Override
	public ObjectiveFunction<? super E, ? super P>[] getObjectives() {
		return objectives;
	}

	@Override
	public Stats[] getResults() {
		return stats;
	}

	@Override
	public double getNormalizedResult() {
		getResults();
		double result;
		switch (criteria) {
		case BEST:
			result = objectives[0].normalize(1) > 0.5 ? stats[0].getMax() : stats[0].getMin();
			break;
		case MEAN:
			result = stats[0].getMean();
			break;
		case WORST:
			result = objectives[0].normalize(1) > 0.5 ? stats[0].getMin() : stats[0].getMax();
			break;
		default:
			throw new IllegalStateException();
		}
		return objectives[0].normalize(result);
	}

	public Predictions<E, P>[][] getPredictions() {
		return predictions;
	}

	@Override
	public String getReport() {
		getResults();
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		final PairList<String, String> info = new PairList<>();
		info.add("Model", learner);
		info.add("Training time", trainingIimeInMillis + " ms");
		info.add("Prediction time", predictionIimeInMillis + " ms");
		for (int i = 0; i < objectives.length; ++i) {
			info.add(objectives[i].getName(),
					formatter.format(stats[i].getMean()) + " \u00B1" + formatter.format(stats[i].getStandardDeviation())
							+ " [" + formatter.format(stats[i].getMin()) + "," + formatter.format(stats[i].getMax())
							+ "]");
		}
		// builder.addValue(UiUtils.format(getFeatures()));
		return UiUtils.format(info);
	}

}
