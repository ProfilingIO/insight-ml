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

import com.insightml.data.samples.Sample;
import com.insightml.models.Predictions;

public final class MeanAbsoluteError extends AbstractObjectiveFunctionFrame<Number, Number> {

	private static final long serialVersionUID = 3835286001852872579L;

	private final double min;
	private final double max;

	public MeanAbsoluteError() {
		this(-99999999, 9999999);
	}

	public MeanAbsoluteError(final double min, final double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public DescriptiveStatistics acrossLabels(
			final List<? extends Predictions<? extends Number, ? extends Number>>[] predictions) {
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		for (final List<? extends Predictions<? extends Number, ? extends Number>> predz : predictions) {
			for (final Predictions<? extends Number, ? extends Number> preds : predz) {
				final Number[] pred = preds.getPredictions();
				final Number[] exp = preds.getExpected();
				final int labelIndex = preds.getLabelIndex();
				for (int i = 0; i < pred.length; ++i) {
					if (exp[i] != null) {
						stats.addValue(instance(pred[i], exp[i], preds.getSample(i), labelIndex));
					}
				}
			}
		}
		return stats;
	}

	@Override
	public double instance(final Number prediction, final Number label, final Sample sample, final int labelIndex) {
		final double pred = Math.min(max, Math.max(min, prediction.doubleValue()));
		return Math.abs(label.doubleValue() - pred);
	}

	@Override
	public double normalize(final double score) {
		return -score;
	}

}
