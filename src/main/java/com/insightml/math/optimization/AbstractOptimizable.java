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
package com.insightml.math.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer.PopulationSize;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer.Sigma;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.MultiDirectionalSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.random.MersenneTwister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.math.Vectors;
import com.insightml.utils.Check;
import com.insightml.utils.types.Triple;
import com.insightml.utils.ui.SimpleFormatter;

public abstract class AbstractOptimizable implements Optimizable {
	private static final boolean ADDITIONAL_OPTIMIZERS = false;

	private final Convergence convergence;
	private final SimpleBounds bounds;

	private final boolean log;
	private final Logger logger = LoggerFactory.getLogger(AbstractOptimizable.class);

	public AbstractOptimizable(final int maxIt, final double precision) {
		this(maxIt, precision, true);
	}

	private AbstractOptimizable(final int maxIt, final double precision, final boolean log) {
		this(maxIt, precision, null, null, null, log);
	}

	public AbstractOptimizable(final int maxIt, final double precision, final double lower, final double upper) {
		this(maxIt, precision, new double[] { lower }, new double[] { upper }, null, false);
	}

	public AbstractOptimizable(final int maxIt, final double precision, final double[] lower, final double[] upper) {
		this(maxIt, precision, lower, upper, null, true);
	}

	public AbstractOptimizable(final int maxIt, final double precision, final double[] lower, final double[] upper,
			final Double trainMax, final boolean log) {
		convergence = new Convergence(maxIt, trainMax, precision);
		bounds = lower == null ? null : new SimpleBounds(lower, upper);
		this.log = log;
	}

	public final Triple<double[], Double, Double> max() {
		return max(bounds.getLower());
	}

	public final Triple<double[], Double, Double> max(final double[] initial) {
		return max(null, initial);
	}

	@Override
	public final Triple<double[], Double, Double> max(final MultivariateFunction test, final double[] initial) {
		Check.size(initial, 1, 999);
		final double initialTrain = value(initial);
		final double initialTest = test == null ? Double.NEGATIVE_INFINITY : test.value(initial);
		PointValuePair result = new PointValuePair(initial, initialTrain);
		Triple<double[], Double, Double> bestTrain = Triple.create(initial, initialTrain, initialTest);
		Triple<double[], Double, Double> bestTest = Triple.create(initial, initialTrain, initialTest);

		while (true) {
			result = iteration(result);
			if (result.getSecond() < bestTrain.getSecond() + convergence.getAbsoluteThreshold()) {
				log("RESULT", result);
				break;
			}
			final double testScore = test == null ? 0 : test.value(result.getFirst());
			bestTrain = Triple.create(result.getFirst(), result.getSecond(), testScore);
			if (test != null && testScore > bestTest.getThird()) {
				bestTest = bestTrain;
			}
			// todo: prevent doing NM twice
			if (bounds == null) {
				break;
			}
		}

		if (test == null) {
			return bestTrain;
		}
		final double improveTrain = bestTrain.getSecond() - bestTest.getSecond();
		final double improveTest = bestTest.getThird() - bestTrain.getThird();
		if (improveTest > improveTrain) {
			logger.info(bestTrain + " vs. " + bestTest);
		}
		return improveTest > improveTrain ? bestTest : bestTrain;
	}

	private PointValuePair iteration(final PointValuePair init) {
		PointValuePair result = init;
		if (bounds != null) {
			result = select("CM", cmaes(init.getFirst(), 10), result);
		}
		result = select("NM", nelderMead(init.getFirst()), result);
		if (ADDITIONAL_OPTIMIZERS) {
			result = select("MD", multiDirection(init.getFirst()), result);
			result = select("PO", powell(init.getFirst()), result);
			try {
				result = select("BO", bobyqa(init.getFirst()), result);
			} catch (final Exception e) {
				logger.error("{}", e);
			}
		}
		return result;
	}

	private PointValuePair select(final String method, final PointValuePair newResult, final PointValuePair oldResult) {
		if (newResult.getValue() < oldResult.getValue()
				|| convergence.trainMax != null && newResult.getValue() > convergence.trainMax) {
			log(method + " (rejc.)", newResult);
			return oldResult;
		}
		log(method, newResult);
		return newResult;
	}

	private PointValuePair nelderMead(final double[] initialValues) {
		return simplex(new NelderMeadSimplex(initialValues.length, 1, 2, 0.5, 0.5), initialValues);
	}

	private PointValuePair multiDirection(final double[] initialValues) {
		return simplex(new MultiDirectionalSimplex(initialValues.length, 2, 0.5), initialValues);
	}

	private PointValuePair simplex(final AbstractSimplex simplex, final double[] initialValues) {
		return optimize(new SimplexOptimizer(conv()), initialValues, simplex);
	}

	private PointValuePair bobyqa(final double[] initialValues) {
		return optimize(new BOBYQAOptimizer(2 * initialValues.length), initialValues);
	}

	private PointValuePair cmaes(final double[] initialValues, final int initialSize) {
		final CMAESOptimizer cmaes = new CMAESOptimizer(30000, 0, true, 1, 0, new MersenneTwister(1334498400 * 1000),
				false, conv());
		return optimize(cmaes,
				initialValues,
				new PopulationSize(initialSize),
				new Sigma(Vectors.fill(0.3, initialValues.length)),
				bounds);
	}

	private PointValuePair powell(final double[] initialValues) {
		final PowellOptimizer powell = new PowellOptimizer(0.0000001, 0.0000001, conv());
		return optimize(powell, initialValues);
	}

	private Convergence conv() {
		return new Convergence(convergence.maxIt, convergence.trainMax, convergence.getAbsoluteThreshold());
	}

	private PointValuePair optimize(final MultivariateOptimizer optimizer, final double[] initialValues,
			final OptimizationData... data) {
		final OptimizationData[] d = new OptimizationData[5 + data.length];
		d[0] = new MaxIter(convergence.maxIt + 1);
		d[1] = new MaxEval(convergence.maxIt * 2);
		d[2] = new ObjectiveFunction(this);
		d[3] = GoalType.MAXIMIZE;
		d[4] = new InitialGuess(fixBounds(initialValues));
		for (int i = 0; i < data.length; ++i) {
			d[5 + i] = data[i];
		}
		return optimizer.optimize(d);
	}

	private double[] fixBounds(final double[] old) {
		if (bounds == null) {
			return old;
		}
		final double[] fix = new double[old.length];
		for (int i = 0; i < old.length; ++i) {
			fix[i] = Math.max(bounds.getLower()[i], Math.min(bounds.getUpper()[i], old[i]));
		}
		return fix;
	}

	private void log(final String method, final PointValuePair results) {
		if (log) {
			final SimpleFormatter format = new SimpleFormatter(5, true);
			logger.info(method + ": " + format.format(results.getKey()) + ", " + format.format(results.getValue()));
		}
	}
}
