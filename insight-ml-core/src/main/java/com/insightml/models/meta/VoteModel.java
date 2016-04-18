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
package com.insightml.models.meta;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.models.IModel;
import com.insightml.utils.Arrays;
import com.insightml.utils.jobs.AbstractJob;
import com.insightml.utils.jobs.IJobBatch;
import com.insightml.utils.jobs.ThreadedClient;

public final class VoteModel<I extends ISample> extends AbstractEnsembleModel<I, Double> {

	private static final long serialVersionUID = -8515840219123634452L;

	public enum VoteStrategy {
		AVERAGE, MEDIAN, GEOMETRIC, HARMONIC
	}

	private VoteStrategy strategy;

	VoteModel() {
	}

	public VoteModel(final IModel<I, Double>[] models, final double[] weights, final VoteStrategy strategy) {
		super(models, weights);
		this.strategy = strategy;
	}

	@Override
	public Double[] apply(final ISamples<I, ?> instnces) {
		final IModel<I, Double>[] models = getModels();
		final double[] weights = getWeights();
		final IJobBatch<Object> batch = new ThreadedClient().newBatch();
		final Double[][] predss = new Double[models.length][];
		for (int m = 0; m < models.length; ++m) {
			final int j = m;
			batch.addJob(new AbstractJob<Object>("") {

				private static final long serialVersionUID = -2963052506505226869L;

				@Override
				public Object run() {
					predss[j] = models[j].apply(instnces);
					return null;
				}
			});
		}
		batch.run();
		final DescriptiveStatistics[] map = Arrays.fill(instnces.size(), DescriptiveStatistics.class);
		for (int i = 0; i < predss.length; ++i) {
			for (int j = 0; j < predss[i].length; ++j) {
				map[j].addValue(predss[i][j] * weights[i]);
			}
		}
		final Double[] preds = new Double[map.length];
		for (int i = 0; i < preds.length; ++i) {
			preds[i] = resolve(map[i]);
		}
		return preds;
	}

	private double resolve(final DescriptiveStatistics stats) {
		switch (strategy) {
		case AVERAGE:
			return stats.getMean();
		case MEDIAN:
			return stats.getPercentile(50);
		case GEOMETRIC:
			return stats.getGeometricMean();
		case HARMONIC:
			double sum = 0;
			for (final double value : stats.getValues()) {
				sum += 1 / value;
			}
			return stats.getN() * 1.0 / sum;
		default:
			throw new IllegalStateException();
		}
	}
}