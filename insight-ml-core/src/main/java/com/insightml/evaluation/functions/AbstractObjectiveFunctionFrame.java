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

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.ISamples;
import com.insightml.models.Predictions;

public abstract class AbstractObjectiveFunctionFrame<E, P> extends AbstractObjectiveFunction<E, P> {
	private static final long serialVersionUID = -7212322445994504764L;

	@Override
	public DescriptiveStatistics acrossLabels(
			final List<? extends Predictions<? extends E, ? extends P>>[] predictions) {
		double sum = 0;
		double weightSum = 0;
		for (final List<? extends Predictions<? extends E, ? extends P>> predz : predictions) {
			for (final Predictions<? extends E, ? extends P> preds : predz) {
				final int labelIndex = preds.getLabelIndex();
				final E[] exp = preds.getExpected();
				final double[] weights = preds.getSamples().weights(labelIndex);
				for (int i = 0; i < weights.length; ++i) {
					if (exp[i] == null) {
						continue;
					}
					final double weight = weights[i];
					sum += instance(preds.getPredictions()[i], exp[i], preds.getSample(i)) * weight;
					weightSum += weight;
				}
			}
		}
		return new DescriptiveStatistics(new double[] { getResult(sum, weightSum) });
	}

	@Override
	public final DescriptiveStatistics label(final P[] preds, final E[] expectd, final double[] weights,
			final ISamples<?, ?> samples, final int labelIndex) {
		double sum = 0;
		double weightSum = 0;
		for (int i = 0; i < preds.length; ++i) {
			if (expectd[i] != null) {
				sum += instance(preds[i], expectd[i], samples == null ? null : samples.get(i)) * weights[i];
				weightSum += weights[i];
			}
		}
		return new DescriptiveStatistics(new double[] { getResult(sum, weightSum) });
	}

	protected double getResult(final double sum, final double weightSum) {
		return sum / weightSum;
	}

}
