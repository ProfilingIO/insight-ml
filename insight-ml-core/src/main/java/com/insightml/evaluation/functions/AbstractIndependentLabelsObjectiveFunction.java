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

import com.insightml.data.samples.ISample;
import com.insightml.models.Predictions;

public abstract class AbstractIndependentLabelsObjectiveFunction<E, T> extends AbstractObjectiveFunction<E, T> {

	private static final long serialVersionUID = 2875681349138131200L;

	@Override
	public final DescriptiveStatistics acrossLabels(
			final List<? extends Predictions<? extends E, ? extends T>>[] predictions) {
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		for (final List<? extends Predictions<? extends E, ? extends T>> predz : predictions) {
			for (final Predictions<? extends E, ? extends T> preds : predz) {
				for (final double val : label(preds.getPredictions(),
						preds.getExpected(),
						preds.getWeights(),
						preds.getSamples(),
						preds.getLabelIndex()).getValues()) {
					stats.addValue(val);
				}
			}
		}
		return stats;
	}

	@Override
	public final double instance(final T pred, final E exp, final ISample sample) {
		throw new IllegalAccessError();
	}

}
