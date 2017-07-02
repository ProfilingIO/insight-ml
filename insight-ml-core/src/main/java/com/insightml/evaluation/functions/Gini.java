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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;

public final class Gini extends AbstractIndependentLabelsObjectiveFunction<Object, Object> {

	private static final long serialVersionUID = -3739867099887892986L;

	private final boolean normalize;

	public Gini(final boolean normalize) {
		this.normalize = normalize;
	}

	@Override
	public DescriptiveStatistics label(final Object[] preds, final Object[] expected, final double[] weights,
			final ISamples<?, ?> samples, final int labelIndex) {
		final double gini = gini(preds, expected, false);
		return new DescriptiveStatistics(new double[] { normalize ? gini / gini(preds, expected, true) : gini, });
	}

	private static <T> double gini(final T[] preds, final Object[] expected, final boolean doPerfect) {
		final List<Pair<Object, T>> sortedByPdesc = new LinkedList<>();
		for (int i = 0; i < preds.length; ++i) {
			sortedByPdesc.add(new Pair<>(expected[i], preds[i]));
		}
		final boolean isBinary = sortedByPdesc.get(0).getSecond() instanceof Boolean;
		Collections.sort(sortedByPdesc, (o1, o2) -> {
			if (doPerfect) {
				if (isBinary) {
					return ((Boolean) o1.getFirst()).booleanValue() ? -1 : 1;
				}
				return ((Number) o1.getFirst()).doubleValue() >= ((Number) o2.getFirst()).doubleValue() ? -1 : 1;
			}
			return ((Number) o1.getSecond()).doubleValue() >= ((Number) o2.getSecond()).doubleValue() ? -1 : 1;
		});

		double sum = 0;
		double giniSum = 0;
		for (final Pair<Object, T> prediction : sortedByPdesc) {
			if (isBinary) {
				sum += (Boolean) prediction.getFirst() ? 1 : 0;
			} else {
				sum += (Double) prediction.getFirst();
			}
			giniSum += sum;
		}

		giniSum = giniSum / sum - (sortedByPdesc.size() + 1.0) / 2;
		return giniSum / sortedByPdesc.size();
	}

}
