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

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.utils.Arguments;
import com.insightml.utils.IArguments;
import com.insightml.utils.jobs.ParallelFor;

public class Bagging<I extends Sample> extends AbstractEnsembleLearner<I, Object, Double> {
	private final VoteStrategy strategy;

	public Bagging(final IArguments arguments, final ILearner<I, ? extends Object, Double>... learner) {
		super(arguments, learner);
		this.strategy = VoteStrategy.AVERAGE;
	}

	public Bagging(final int bags, final double isample, final double fsample, final VoteStrategy strategy,
			final ILearner<I, ? extends Object, Double>... learner) {
		super(new Arguments("bags", bags, "isample", isample, "fsample", fsample), learner);
		this.strategy = strategy;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("bags", 10.0, 5, 1000);
		args.add("isample", 0.8, 0.1, 1.0);
		args.add("fsample", 0.9, 0.05, 1.0);
		return args;
	}

	@Override
	protected IModel<I, Double> createModel(final ISamples<I, Object> instances,
			final ILearner<I, ? extends Object, Double>[] learner, final int labelIndex) {
		final int bags = (int) argument("bags");
		final double instancesSample = argument("isample");
		final double featureSample = argument("fsample");
		final IModel<I, Double>[] models = new IModel[bags];
		final double[] weights = new double[bags];
		ParallelFor.run(i -> {
			final Random random = new Random((long) Math.pow(i + 2, 2));
			final Pair<ISamples<I, Object>, ISamples<I, Object>> sub = instances.sample(instancesSample, random);
			models[i] = learner[i % learner.length].run(
					new LearnerInput(sub.getFirst().sampleFeatures(featureSample, random), null, null, labelIndex));
			weights[i] = 1;
			return 1;
		}, 0, bags, 9999999);
		return new VoteModel<>(models, weights, strategy);
	}

}
