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
package com.insightml.evaluation.functions;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;
import com.insightml.utils.Collections;

public final class PrecAtRec extends AbstractIndependentLabelsObjectiveFunction<Object, Serializable> {

	private static final long serialVersionUID = -973577388008884354L;

	private final double minRecall;

	public PrecAtRec(final double minRecall) {
		this.minRecall = minRecall;
	}

	@Override
	public String getName() {
		return "Prec@" + minRecall;
	}

	@Override
	public DescriptiveStatistics label(final Serializable[] preds, final Object[] expected, final double[] weights,
			final ISamples<?, ?> samples, final int labelIndex) {
		final List<Pair<Integer, Double>> idxAndPred = new LinkedList<>();
		int totalTrue = 0;
		for (int i = 0; i < preds.length; ++i) {
			final double[] predAndAct = toDouble(preds[i], expected[i]);
			idxAndPred.add(new Pair<>(i, predAndAct[0]));
			if (predAndAct[1] >= 0.5) {
				++totalTrue;
			}
		}
		int totalPreds = 0;
		int correct = 0;
		for (final Pair<Integer, Double> idx : Collections.sortDescending2(idxAndPred)) {
			++totalPreds;
			final int i = idx.getFirst().intValue();
			final double[] predAndAct = toDouble(preds[i], expected[i]);
			if (predAndAct[1] >= 0.5) {
				if (++correct * 1.0 / totalTrue >= minRecall) {
					System.err.println("Stop at " + correct + "/" + totalTrue + " after " + totalPreds + ": " + preds[i]
							+ ", " + expected[i]);
					break;
				}
			}
		}
		return new DescriptiveStatistics(new double[] { correct * 1.0 / totalPreds });
	}
}
