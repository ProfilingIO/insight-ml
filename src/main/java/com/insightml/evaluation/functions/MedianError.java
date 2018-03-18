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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.ISamples;

public final class MedianError extends AbstractIndependentLabelsObjectiveFunction<Object, Serializable> {

	private static final long serialVersionUID = -653970887798242216L;

	@Override
	public DescriptiveStatistics label(final Serializable[] preds, final Object[] expected, final double[] weights,
			final ISamples<?, ?> samples, final int labelIndex) {
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		for (int i = 0; i < preds.length; ++i) {
			final double[] predAndAct = toDouble(preds[i], expected[i]);
			stats.addValue(Math.abs(predAndAct[0] - predAndAct[1]));
		}
		return new DescriptiveStatistics(new double[] { stats.getPercentile(50) });
	}

	@Override
	public double normalize(final double score) {
		return -score;
	}

}
