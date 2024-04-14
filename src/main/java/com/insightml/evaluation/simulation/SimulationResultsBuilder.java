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

import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.evaluation.simulation.SimulationSetup.PERFORMANCE_SELECTOR;
import com.insightml.math.statistics.Stats;
import com.insightml.models.Predictions;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;

import java.util.LinkedList;
import java.util.List;

public class SimulationResultsBuilder<E, P> {
	private final String learner;
	private final ObjectiveFunction<? super E, ? super P>[] objectives;
	private final PERFORMANCE_SELECTOR criteria;

	private int trainingIimeInMillis;
	private int predictionIimeInMillis;

	private final Predictions<E, P>[][] predictions;

	public SimulationResultsBuilder(final String modelName, final int numSets, final int numLabels,
									final SimulationSetup<?, E, P> setup) {
		learner = modelName;
		objectives = setup.getObjectives();
		criteria = setup.getCriteria();
		predictions = new Predictions[Check.num(numSets, 1, 9999)][Check.num(numLabels, 1, 99)];
	}

	public void add(final Predictions<E, P> preds) {
		final int set = Check.num(preds.getSet() - 1, 0, predictions.length - 1);
		Check.isNull(predictions[set][preds.getLabelIndex()]);
		predictions[set][preds.getLabelIndex()] = preds;
		trainingIimeInMillis += preds.getModelTrainingTimeInMillis();
		predictionIimeInMillis += preds.getTimeInMillis();
	}

	public SimulationResults<E, P> build() {
		final Stats[] stats = Arrays.fill(objectives.length, Stats.class);
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
					9999999,
					objectives[m])) {
				stats[m].add(value);
			}
		}
		return new SimulationResults<>(learner, objectives, criteria, predictions, stats, trainingIimeInMillis,
				predictionIimeInMillis);
	}
}
