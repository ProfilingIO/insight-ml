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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.util.Pair;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.decorators.LabelDecorator;
import com.insightml.data.samples.decorators.SamplesMapping;
import com.insightml.evaluation.functions.MSE;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.math.optimization.AbstractOptimizable;
import com.insightml.models.AbstractIndependentFeaturesModel;
import com.insightml.models.DoubleModel;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.regression.SimpleRegression;
import com.insightml.utils.Arguments;
import com.insightml.utils.Arrays;
import com.insightml.utils.IArguments;
import com.insightml.utils.Utils;
import com.insightml.utils.types.DoublePair;

public class GBM extends AbstractEnsembleLearner<Sample, Double, Double> {

	private final ObjectiveFunction<Object, ? super Double> objective;
	private final Baseline predefinedBaseline;

	public GBM(final IArguments arguments, final ObjectiveFunction<? extends Object, ? super Double> objective,
			final ILearner<Sample, Double, Double>[] learner, final Baseline predefinedBaseline) {
		super(arguments, learner);
		this.objective = (ObjectiveFunction<Object, ? super Double>) objective;
		this.predefinedBaseline = predefinedBaseline;
	}

	public GBM(final int it, final double shrink, final double bag,
			final ObjectiveFunction<? extends Object, ? super Double> objective,
			final ILearner<Sample, Double, Double>[] learner) {
		this(new Arguments("it", it, "shrink", shrink, "bag", bag), objective, learner, null);
	}

	@Override
	public LearnerArguments arguments() {
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
	@Nonnull
	public BoostingModel run(final ISamples<? extends Sample, ? extends Double> samples,
			final ISamples<? extends Sample, ? extends Double> valid, final FeaturesConfig<? extends Sample, ?> config,
			final int labelIndex) {
		final Object[] expected = samples.expected(labelIndex);
		final double[] weights = weightSamples((ISamples<Sample, Double>) samples, labelIndex);

		final DoubleModel first = f0(expected, weights, labelIndex);
		double[] preds = first.predictDouble(samples);
		final Random random = Utils.random();
		final int iterations = (int) argument("it");
		final double shrinkage = argument("shrink");
		final List<DoublePair<DoubleModel>> steps = new ArrayList<>(iterations);
		final ILearner<Sample, ? extends Object, Double>[] learner = getLearners();
		for (int i = 0; i < iterations; ++i) {
			final ISamples<Sample, Double> subset = subset((ISamples<Sample, Double>) samples,
					preds,
					expected,
					random,
					labelIndex);
			if (subset == null) {
				continue;
			}
			try {
				final AbstractIndependentFeaturesModel fit = (AbstractIndependentFeaturesModel) learner[i
						% learner.length].run((ISamples) subset, labelIndex);
				final Pair<Double, double[]> update = fitGamma(fit,
						preds,
						samples,
						expected,
						weights,
						i + 1,
						labelIndex);
				steps.add(new DoublePair<>(fit, shrinkage * update.getFirst()));
				preds = update.getSecond();
			} catch (final ConvergenceException e) {
				logger.error("{}", e);
			}
		}
		return new BoostingModel(first, steps);
	}

	@SuppressWarnings("static-method")
	protected double[] weightSamples(final ISamples<Sample, Double> samples, final int labelIndex) {
		return samples.weights(labelIndex);
	}

	private DoubleModel f0(final Object[] exp, final double[] weights, final int labelIndex) {
		if (predefinedBaseline != null) {
			return predefinedBaseline;
		}
		final double[] preds = new double[exp.length];
		final double[] optim = new double[preds.length];
		for (int i = 0; i < optim.length; ++i) {
			optim[i] = 1;
		}
		final double result = findGamma(exp, weights, preds, optim, 0.0000000001, labelIndex);
		logger.info("[0] " + objective
				.label(Arrays.cast(updatePredictions(preds, optim, result)), exp, weights, null, labelIndex).getMean());
		return new Baseline(result);
	}

	private ISamples<Sample, Double> subset(final ISamples<Sample, Double> instances, final double[] preds,
			final Object[] expected, final Random random, final int labelIndex) {
		final Pair<SamplesMapping<Sample, Double>, double[]> error = sampleError(instances, preds, expected, random);
		return new LabelDecorator<>(error.getFirst(), Arrays.cast(error.getSecond()), labelIndex)
				.sampleFeatures(argument("fbag"), random);
	}

	private Pair<Double, double[]> fitGamma(final DoubleModel fit, final double[] preds, final ISamples<?, ?> instances,
			final Object[] expected, final double[] weights, final int it, final int labelIndex) {
		final double[] optim = fit.predictDouble(instances);
		final double gamma = findGamma(expected, weights, preds, optim, 0.000000001, labelIndex);
		final double[] update = updatePredictions(preds, optim, gamma * argument("shrink"));
		if (it % 100 == 0) {
			logger.info("[" + it + "] "
					+ objective.label(Arrays.cast(update), expected, weights, instances, labelIndex).getMean());
		}
		return new Pair<>(gamma, update);
	}

	private double findGamma(final Object[] exp, final double[] weights, final double[] preds, final double[] optim,
			final double prec, final int labelIndex) {
		return objective instanceof MSE ? fitSquares(exp, weights, preds, optim)
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
		// TODO: add analytical solutions for more objectives
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
