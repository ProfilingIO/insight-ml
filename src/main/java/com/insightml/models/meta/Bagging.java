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

import java.util.Random;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.utils.Arguments;
import com.insightml.utils.IArguments;

public class Bagging<I extends Sample> extends AbstractEnsembleLearner<I, Double, Double> {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final VoteStrategy strategy;

	public Bagging(final IArguments arguments, final ILearner<I, Double, Double>... learner) {
		super(arguments, learner);
		this.strategy = VoteStrategy.AVERAGE;
	}

	public Bagging(final int bags, final double isample, final double fsample, final VoteStrategy strategy,
			final ILearner<I, Double, Double>... learner) {
		super(new Arguments("bags", bags, "isample", isample, "fsample", fsample), learner);
		this.strategy = strategy;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("bags", 10.0, 2, 1000);
		args.add("isample", 0.8, 0.1, 1.0);
		args.add("fsample", 0.9, 0.05, 1.0);
		return args;
	}

	@Override
	public IModel<I, Double> run(final ISamples<I, Double> train, final int labelIndex) {
		final ISamples<I, Double> samples = preprocess(train);
		final int bags = (int) argument("bags");
		final double instancesSample = argument("isample");
		final double featureSample = argument("fsample");
		final IModel<I, Double>[] models = new IModel[bags];
		final double[] weights = new double[bags];
		final ILearner<I, Double, Double>[] learner = getLearners();
		for (int i = 0; i < bags; ++i) {
			LOG.info("Running step {}/{}", i + 1, bags);
			models[i] = model(labelIndex, samples, instancesSample, featureSample, learner, i);
			weights[i] = 1;
		}
		return new VoteModel<>(models, weights, strategy);
	}

	private IModel<I, Double> model(final int labelIndex, final ISamples<I, Double> samples,
			final double instancesSample, final double featureSample, final ILearner<I, Double, Double>[] learner,
			final int i) {
		final Random random = new Random((long) Math.pow(i + 2, 2));
		final ISamples<I, Double> sampled = sample(samples, instancesSample, featureSample, random);
		return learner[i % learner.length].run(sampled, null, null, labelIndex);
	}

	@Override
	public IModel<I, Double> run(final LearnerInput<? extends I, ? extends Double> input) {
		return run((ISamples<I, Double>) input.getTrain(), input.labelIndex);
	}

	@Override
	public IModel<I, Double> run(final ISamples<? extends I, ? extends Double> train,
			final ISamples<? extends I, ? extends Double> valid, final FeaturesConfig<? extends I, ?> config,
			final int labelIndex) {
		return run((ISamples<I, Double>) train, labelIndex);
	}

	protected ISamples<I, Double> sample(final ISamples<I, Double> samples, final double instancesSample,
			final double featureSample, final Random random) {
		final ISamples<I, Double> sub = instancesSample < 1 ? samples.sample(instancesSample, random).getFirst()
				: samples;
		return featureSample < 1 ? sub.sampleFeatures(featureSample, random) : sub;
	}

	protected ISamples<I, Double> preprocess(final @Nonnull ISamples<I, Double> instances) {
		return instances;
	}

}
