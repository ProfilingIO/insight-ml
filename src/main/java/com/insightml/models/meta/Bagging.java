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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import com.insightml.models.trees.RegTree;
import com.insightml.utils.Arguments;
import com.insightml.utils.IArguments;

public class Bagging<I extends Sample> extends AbstractEnsembleLearner<I, Double, Double> {
	private static final long serialVersionUID = -259781296518750592L;

	public static final String FSAMPLE = "fsample";

	private VoteStrategy strategy;
	private ILearner<I, Double, Double>[] learners;

	Bagging() {
	}

	public Bagging(final IArguments arguments, final ILearner<I, Double, Double>... learner) {
		super(arguments);
		strategy = VoteStrategy.AVERAGE;
		learners = learner;
	}

	public Bagging(final int bags, final double isample, final double fsample, final VoteStrategy strategy,
			final ILearner<I, Double, Double>... learner) {
		super(new Arguments("bags", bags, "isample", isample, FSAMPLE, fsample));
		this.strategy = strategy;
		learners = learner;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("bags", 10.0, 2, 1000);
		args.add("isample", 0.8, 0.1, 1.0);
		args.add(FSAMPLE, 0.9, 0.025, 1.0);
		return args;
	}

	@Override
	public IModel<I, Double> run(final LearnerInput<? extends I, ? extends Double> input) {
		return run((ISamples<I, Double>) input.getTrain(), input.labelIndex);
	}

	@Override
	public IModel<I, Double> run(final ISamples<I, Double> train, final int labelIndex) {
		final ISamples<I, Double> samples = preprocess(train);
		final int bags = getNumberOfBags();
		final List<BagResult<I>> result = new ArrayList<>(bags);
		final Logger log = LoggerFactory.getLogger(getClass());
		for (int i = 0; i < bags; ++i) {
			log.info("Running bag {}/{}", i + 1, bags);
			result.add(new BagResult<>(i, computeBag(i, samples, labelIndex), 1));
		}
		return combine(result, strategy, samples.featureNames());
	}

	public ISamples<I, Double> preprocess(@Nonnull final ISamples<I, Double> instances) {
		return instances;
	}

	public int getNumberOfBags() {
		return (int) argument("bags");
	}

	public IModel<I, Double> computeBag(final int index, final ISamples<I, Double> samples, final int labelIndex) {
		final ILearner<I, Double, Double> learner = learners[index % learners.length];
		final double instancesSample = argument("isample");
		final double featureSample = argument(FSAMPLE);
		final Random random = new Random((long) Math.pow(index + 2, 2));
		if (learner instanceof RegTree) {
			return (IModel<I, Double>) ((RegTree) learner).run(
					(ISamples) (instancesSample < 1 ? samples.sample(instancesSample, random).getFirst() : samples),
					GBM.featuresMask(samples.featureNames(), featureSample, getOriginalArguments(), random),
					labelIndex);
		}
		final ISamples<I, Double> sampled = sample(samples, instancesSample, featureSample, random);
		return learner.run(sampled, null, null, labelIndex);
	}

	public static <I extends Sample> VoteModel<I> combine(final Collection<BagResult<I>> bags,
			final VoteStrategy strategy, final String[] features) {
		final IModel<I, Double>[] models = new IModel[bags.size()];
		final double[] weights = new double[models.length];
		for (final BagResult<I> bag : bags) {
			final int i = bag.index;
			models[i] = bag.model;
			weights[i] = bag.weight;
		}
		return new VoteModel<>(models, weights, strategy, features);
	}

	protected ISamples<I, Double> sample(final ISamples<I, Double> samples, final double instancesSample,
			final double featureSample, final Random random) {
		final ISamples<I, Double> sub = instancesSample < 1 ? samples.sample(instancesSample, random).getFirst()
				: samples;
		return featureSample < 1 ? sub.sampleFeatures(featureSample, random) : sub;
	}

	@Override
	public IModel<I, Double> run(final ISamples<? extends I, ? extends Double> train,
			final ISamples<? extends I, ? extends Double> valid, final FeaturesConfig<? extends I, ?> config,
			final int labelIndex) {
		return run((ISamples<I, Double>) train, labelIndex);
	}

	public VoteStrategy getVoteStrategy() {
		return strategy;
	}

	public ISamples<I, Double> sample(final ISamples<I, Double> samples, final double instancesSample,
			final double featureSample, final int i) {
		final Random random = new Random((long) Math.pow(i + 2, 2));
		return sample(samples, instancesSample, featureSample, random);
	}

	public static final class BagResult<I extends Sample> implements Serializable {
		private static final long serialVersionUID = 7284489943210164551L;

		private final int index;
		private final IModel<I, Double> model;
		private final double weight;

		public BagResult(final int index, final IModel<I, Double> model, final double weight) {
			this.index = index;
			this.model = model;
			this.weight = weight;
		}
	}

}
