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

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.Sample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.decorators.LabelDecorator;
import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.math.optimization.AbstractOptimizable;
import com.insightml.models.ILearner;
import com.insightml.models.IModel;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.models.general.ConstantModel;
import com.insightml.models.regression.SimpleRegression;
import com.insightml.utils.Arguments;
import com.insightml.utils.Arrays;
import com.insightml.utils.IArguments;
import com.insightml.utils.Utils;
import com.insightml.utils.types.collections.PairList;

public class GBM extends AbstractEnsembleLearner<Sample, Object, Double> {

	final ObjectiveFunction<Object, ? super Double> objective;

	public GBM(final IArguments arguments, final ObjectiveFunction<Object, ? super Double> objective,
			final ILearner<Sample, Double, Double>[] learner) {
		super(arguments, learner);
		this.objective = objective;
	}

	public GBM(final int it, final double shrink, final double bag,
			final ObjectiveFunction<Object, ? super Double> objective,
			final ILearner<Sample, Double, Double>[] learner) {
		this(new Arguments("it", it, "shrink", shrink, "bag", bag), objective, learner);
	}

	@Override
	public final LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("it", 400.0, 20, 20000);
		args.add("shrink", 0.01, 0.0001, 0.2);
		args.add("bag", 0.5, 0.05, 0.9);
		args.add("fbag", 1.0, 0.1, 1.0);
		return args;
	}

	@Override
	public final String getName() {
		return super.getName() + getLearners()[0].getName();
	}

	@Override
	protected final BoostingModel createModel(final ISamples<Sample, Object> samples,
			final ILearner<Sample, ? extends Object, Double>[] learner, final int labelIndex) {
		final Object[] expected = samples.expected(labelIndex);
		final IModel<Sample, Double> first = f0(expected, samples.weights(labelIndex), labelIndex);
		final PairList<IModel<Sample, Double>, Double> steps = new PairList<>(true);
		double[] preds = Arrays.cast(first.apply(samples));
		final Random random = Utils.random();
		final int iterations = (int) argument("it");
		final double shrinkage = argument("shrink");
		for (int i = 0; i < iterations; ++i) {
			final ISamples<Sample, Object> subset = subset(samples, preds, expected, random, labelIndex);
			if (subset == null) {
				continue;
			}
			try {
				final IModel<Sample, Double> fit = learner[i % learner.length]
						.run(new LearnerInput(subset, null, labelIndex));
				final Pair<Double, double[]> update = fitGamma(fit, preds, samples, i + 1, labelIndex);
				steps.add(fit, shrinkage * update.getFirst());
				preds = update.getSecond();
			} catch (final ConvergenceException e) {
				logger.error("{}", e);
			}
		}
		return new BoostingModel(first, steps);
	}

	private ConstantModel<Double> f0(final Object[] exp, final double[] weights, final int labelIndex) {
		final double[] preds = new double[exp.length];
		final double[] optim = new double[preds.length];
		for (int i = 0; i < optim.length; ++i) {
			optim[i] = 1;
		}
		final double result = findGamma(exp, weights, preds, optim, 0.0000000001, labelIndex);
		logger.info("[0] " + objective
				.label(Arrays.cast(updatePredictions(preds, optim, result)), exp, weights, null, labelIndex).getMean());
		return new ConstantModel<>(result);
	}

	private ISamples<Sample, Object> subset(final ISamples<Sample, Object> instances, final double[] preds,
			final Object[] expected, final Random random, final int labelIndex) {
		final Pair<SamplesMapping<Sample, Object>, double[]> error = sampleError(instances, preds, expected, random);
		return new LabelDecorator<>(error.getFirst(), Arrays.cast(error.getSecond()), labelIndex)
				.sampleFeatures(argument("fbag"), random);
	}

	private Pair<Double, double[]> fitGamma(final IModel<Sample, Double> fit, final double[] preds,
			final ISamples<Sample, Object> instances, final int it, final int labelIndex) {
		final double[] optim = Arrays.cast(fit.apply(instances));
		final double gamma = findGamma(instances.expected(labelIndex), instances.weights(labelIndex), preds, optim,
				0.000000001, labelIndex);
		final double[] update = updatePredictions(preds, optim, gamma * argument("shrink"));
		if (it % 100 == 0) {
			logger.info("[" + it + "] " + objective.label(Arrays.cast(update), instances.expected(labelIndex),
					instances.weights(labelIndex), instances, labelIndex).getMean());
		}
		return new Pair<>(gamma, update);
	}

	private double findGamma(final Object[] exp, final double[] weights, final double[] preds, final double[] optim,
			final double prec, final int labelIndex) {
		return true ? fitSquares(exp, weights, preds, optim)
				: fitObjective(exp, weights, preds, optim, prec, labelIndex);
	}

	private static double fitSquares(final Object[] expected, final double[] weights, final double[] preds,
			final double[] optim) {
		final SimpleRegression reg = new SimpleRegression(false);
		for (int i = 0; i < preds.length; ++i) {
			reg.addData(optim[i], Utils.toDouble(expected[i]) - preds[i], weights[i]);
		}
		return reg.regress()[0];
	}

	private double fitObjective(final Object[] exp, final double[] weights, final double[] preds, final double[] optim,
			final double prec, final int labelIndex) {
		return new AbstractOptimizable(10000, prec, -9999, 9999) {
			@Override
			public double value(final double[] point) {
				return objective.normalize(objective
						.label(Arrays.cast(updatePredictions(preds, optim, point[0])), exp, weights, null, labelIndex)
						.getMean());
			}
		}.max().getFirst()[0];
	}

	static double[] updatePredictions(final double[] lastModel, final double[] update, final double gamma) {
		final double[] preds = lastModel.clone();
		for (int i = 0; i < update.length; ++i) {
			preds[i] += gamma * update[i];
		}
		return preds;
	}

}
