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

import java.io.File;

import com.insightml.data.samples.Sample;
import com.insightml.models.DistributionPrediction;
import com.insightml.models.IModelPipeline;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.utils.io.serialization.Serialization;
import com.insightml.utils.pipeline.PipelineSource;

final class MajorityVoteModel<I extends Sample> implements IModelPipeline<I, Double> {
	private static final long serialVersionUID = 7321467505416315945L;

	private final IModelPipeline<I, Double>[] models;
	private final double[] weights;
	private final VoteStrategy strategy;

	private final String cacheDir;

	public MajorityVoteModel(final IModelPipeline<I, Double>[] models, final double[] weightsFixed,
			final VoteStrategy strategy, final String cacheDir) {
		this.models = models;
		this.weights = weightsFixed;
		this.strategy = strategy;
		this.cacheDir = cacheDir;
	}

	@Override
	public Double[] run(final Iterable<? extends I> test) {
		final Double[][] predss = new Double[weights.length][];
		for (int i = 0; i < models.length; ++i) {
			predss[i] = new ModelPrediction<>(models[i], test, cacheDir).get();
		}
		return VoteModel.ensemble(weights, predss, strategy);
	}

	@Override
	public DistributionPrediction[] predictDistribution(final Iterable<? extends I> samples, final boolean debug) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLabelIndex() {
		return models[0].getLabelIndex();
	}

	@Override
	public void close() {
		for (final IModelPipeline<I, Double> model : models) {
			model.close();
		}
	}

	@Override
	public String info() {
		return null;
	}

	private static final class ModelPrediction<I extends Sample> extends PipelineSource<Double[]> {

		private final IModelPipeline<I, Double> model;
		private final Iterable<? extends I> data;

		public ModelPrediction(final IModelPipeline<I, Double> model, final Iterable<? extends I> data,
				final String cacheDir) {
			this.model = model;
			this.data = data;

			if (cacheDir != null) {
				serializeResult(new File(cacheDir + "/predictions/" + model.hashCode() + "_" + data.hashCode()),
						Serialization.get());
				loadSerializedResultsIfAvailable(Double[].class);
			}
		}

		@Override
		protected Double[] load() {
			return model.run(data);
		}
	}
}