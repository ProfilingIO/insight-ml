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
package com.insightml.math.distributions;

import org.apache.commons.math3.util.FastMath;

import com.google.gson.annotations.Expose;
import com.insightml.math.types.Interval;
import com.insightml.utils.ui.SimpleFormatter;

public final class GaussianDistribution extends AbstractGaussian {
	private static final long serialVersionUID = -7890800047310199392L;

	@Expose
	private final double mean;
	@Expose
	private final double stddev;

	private final double sigmaSquare;
	private final double factor;

	public GaussianDistribution(final double mean, final double stddev) {
		this.mean = mean;
		this.stddev = stddev;

		sigmaSquare = stddev * stddev;
		factor = 1. / Math.sqrt(2 * Math.PI * sigmaSquare);
	}

	public GaussianDistribution(final double[] values) {
		this(values, false);
	}

	public GaussianDistribution(final double[] values, final boolean biasedVariance) {
		final int n = values.length;
		double sum = 0;
		double sumSquared = 0;
		for (final double value : values) {
			sum += value;
			sumSquared += value * value;
		}

		mean = sum / values.length;
		if (biasedVariance) {
			sigmaSquare = sumSquared / n - mean * mean;
		} else {
			sigmaSquare = sumSquared / (n - 1) - n * mean * mean / (n - 1);
		}
		stddev = Math.sqrt(sigmaSquare);

		factor = 1. / Math.sqrt(2 * Math.PI * sigmaSquare);
	}

	@Override
	public double probability(final double x) {
		final double diff = x - mean;
		if (sigmaSquare == 0) {
			return diff == 0 ? 1 : 0;
		}
		return factor * (diff == 0 ? 1 : Math.exp(-diff * diff / (2 * sigmaSquare)));
	}

	@Override
	public double logLikelihood(final double x) {
		// TODO: define better way to handle zero/min likelihood
		final double likelihood = probability(x);
		return FastMath.log(Math.max(0.0000000000000000001, likelihood));
	}

	@Override
	public double expectedValue() {
		return mean;
	}

	@Override
	public double standardDeviation() {
		return stddev;
	}

	@Override
	public double maxLikelihood() {
		return mean;
	}

	@Override
	public Interval confidenceInterval(final double factorStddev) {
		final double rangeMax = mean + stddev * factorStddev;
		final double rangeMin = mean - stddev * factorStddev;
		return new Interval(rangeMin, rangeMax);
	}

	// http://www.allisons.org/ll/MML/KL/Normal/
	public double klDivergence(final GaussianDistribution other) {
		return (Math.pow(mean - other.mean, 2) + sigmaSquare - other.sigmaSquare) / (2 * other.sigmaSquare)
				+ Math.log(Math.sqrt(other.sigmaSquare) / Math.sqrt(sigmaSquare));
	}

	@Override
	public String toString() {
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		return "Gaussian{" + formatter.format(mean) + " +/- " + formatter.format(stddev) + ", "
				+ formatter.format(sigmaSquare) + "}";
	}

}
