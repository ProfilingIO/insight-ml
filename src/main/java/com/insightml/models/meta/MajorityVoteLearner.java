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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.Vectors;
import com.insightml.models.ILearner;
import com.insightml.models.ILearnerPipeline;
import com.insightml.models.IModel;
import com.insightml.models.IModelPipeline;
import com.insightml.models.LearnerPipeline;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.io.serialization.Serialization;
import com.insightml.utils.ui.SimpleFormatter;

public class MajorityVoteLearner<I extends Sample> extends AbstractEnsembleLearner<I, Object, Double> {
	private static final long serialVersionUID = 3931887579996285642L;

	private static final Logger LOG = LoggerFactory.getLogger(MajorityVoteLearner.class);

	private final ILearner<I, Object, Double>[] learners;
	private final VoteStrategy strategy;
	private final double[] weights;
	private final String cacheDir;

	public MajorityVoteLearner(final ILearner<I, Object, Double>[] learners, final VoteStrategy strategy,
			final double[] weights, final IArguments arguments, final String cacheDir) {
		super(arguments);
		this.learners = learners;
		this.strategy = Check.notNull(strategy);
		final double weightSum = Vectors.sum(weights);
		this.weights = weights.clone();
		for (int i = 0; i < weights.length; ++i) {
			this.weights[i] /= weightSum;
		}
		this.cacheDir = cacheDir;
	}

	@Override
	public String getName() {
		final DecimalFormat formatter = SimpleFormatter.createDecimalFormatter(4);
		return strategy.name().substring(0, 3) + "{"
				+ Arrays.stream(weights).mapToObj(v -> formatter.format(v)).collect(Collectors.joining(",")) + "}"
				+ makeLearnersName(learners);
	}

	public static String makeLearnersName(final ILearner<?, ?, ?>[] learners) {
		final StringBuilder name = new StringBuilder();
		for (final ILearner<?, ?, ?> learner : learners) {
			name.append(", ");
			name.append(learner.getName());
		}
		return "[" + name.substring(2) + "]";
	}

	@Override
	public IModelPipeline<I, Double> run(final Iterable<I> data, final Iterable<I> unlabled,
			final FeaturesConfig<? extends I, Double> config, final int labelIndex,
			final ILearnerPipeline<I, Double> learnerPipe) {
		final IModelPipeline<I, Double>[] models = trainModels(data, unlabled, config, labelIndex, learners);
		final double[] weightsFixed = new double[models.length];
		double weightSum = 0;
		for (int i = 0; i < weightsFixed.length; ++i) {
			weightsFixed[i] = weights[i];
			weightSum += weights[i];
		}
		for (int i = 0; i < weightsFixed.length; ++i) {
			weightsFixed[i] /= weightSum;
		}
		return new MajorityVoteModel<>(models, weightsFixed, strategy, cacheDir);
	}

	public static <I extends Sample> IModelPipeline<I, Double>[] trainModels(final Iterable<I> data,
			final Iterable<I> unlabled, final FeaturesConfig<? extends I, Double> config, final int labelIndex,
			final ILearner<I, ? extends Object, Double>[] learners) {
		final IModelPipeline<I, Double>[] models = new IModelPipeline[learners.length];
		for (int i = 0; i < models.length; ++i) {
			LOG.info("Training {}", learners[i].getName());
			models[i] = new LearnerPipeline<>(learners[i], 1.0, true, Serialization.get())
					.run(data, unlabled, config, labelIndex);
		}
		return models;
	}

	@Override
	public IModel<I, Double> run(final ISamples<? extends I, ? extends Object> samples,
			final ISamples<? extends I, ? extends Object> valid, final FeaturesConfig<? extends I, ?> config,
			final int labelIndex) {
		throw new UnsupportedOperationException();
	}

}
