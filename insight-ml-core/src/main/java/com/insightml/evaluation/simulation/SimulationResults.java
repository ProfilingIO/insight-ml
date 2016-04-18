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
import java.util.LinkedList;
import java.util.List;

import com.insightml.data.samples.ISample;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.evaluation.simulation.ISimulationSetup.PERFORMANCE_SELECTOR;
import com.insightml.math.statistics.Stats;
import com.insightml.models.Predictions;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.collections.PairList;
import com.insightml.utils.ui.SimpleFormatter;
import com.insightml.utils.ui.UiUtils;

public final class SimulationResults<I extends ISample, E, P> extends AbstractClass implements Serializable,
ISimulationResults<E, P> {

	private static final long serialVersionUID = 2715905427176152958L;

	private final String learner;
	private final IObjectiveFunction<? super E, ? super P>[] objectives;
	private final PERFORMANCE_SELECTOR criteria;

	private final long start;
	private int seconds;

	private final Predictions<E, P>[][] predictions;
	private Stats[] stats;

	public SimulationResults(final int numSets, final int numLabels, final ISimulationSetup<I, E, P> setup) {
		learner = setup.getLearner()[0].getName();
		this.objectives = setup.getObjectives();
		criteria = setup.getCriteria();
		predictions = new Predictions[Check.num(numSets, 1, 9999)][Check.num(numLabels, 1, 99)];
		start = System.currentTimeMillis();
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
	public IObjectiveFunction<? super E, ? super P>[] getObjectives() {
		return objectives;
	}

	public void add(final Predictions<E, P> preds) {
		Check.state(stats == null);
		final int set = Check.num(preds.getSet() - 1, 0, predictions.length - 1);
		Check.isNull(predictions[set][preds.getLabelIndex()]);
		predictions[set][preds.getLabelIndex()] = preds;
	}

	@Override
	public Stats[] getResults() {
		if (stats == null) {
			if (seconds == 0) {
				seconds = (int) (System.currentTimeMillis() - start) / 1000;
			}
			stats = Arrays.fill(objectives.length, Stats.class);
			final List<Predictions<E, P>>[] preds = new List[predictions[0].length];
			for (final Predictions<E, P>[] run : predictions) {
				for (int i = 0; i < run.length; ++i) {
					if (preds[i] == null) {
						preds[i] = new LinkedList<>();
					}
					preds[i].add(run[i]);
				}
			}
			for (int m = 0; m < objectives.length; ++m) {
				for (final double value : Check.size(objectives[m].acrossLabels(preds).getValues(),
						1,
						999999,
						objectives[m])) {
					stats[m].add(value);
				}
			}
		}
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
		return predictions.clone();
	}

	public String getReport() {
		getResults();
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		final PairList<String, String> info = new PairList<>();
		info.add("Model", learner);
		info.add("Duration", seconds + " seconds");
		for (int i = 0; i < objectives.length; ++i) {
			info.add(objectives[i].getName(),
					formatter.format(stats[i].getMean()) + " \u00B1"
							+ formatter.format(stats[i].getStandardDeviation()) + " ["
							+ formatter.format(stats[i].getMin()) + "," + formatter.format(stats[i].getMax()) + "]");
		}
		// builder.addValue(UiUtils.format(getFeatures()));
		return UiUtils.format(info);
	}

}
