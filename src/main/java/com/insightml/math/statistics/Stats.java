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
package com.insightml.math.statistics;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.insightml.math.distributions.GaussianDistribution;
import com.insightml.math.types.Interval;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.SimpleFormatter;

public final class Stats extends AbstractClass implements MutableStatistics, Serializable {
	private static final long serialVersionUID = 5899748228598382827L;

	private int n;
	private double sum;
	private double sumWeighted;
	private double min = Double.MAX_VALUE;
	// We don't use a special value like negative infinity here because some serialization libraries have problems with
	// it
	private double max = -1999999999;

	private double mean;
	private double m2;
	private double sumweight;

	public Stats() {
	}

	public Stats(final double[] values) {
		for (final double value : values) {
			add(value, 1.0);
		}
	}

	public void add(final double value) {
		add(value, 1.0);
	}

	@Override
	public void add(final double value, final double weight) {
		Preconditions.checkArgument(weight > 0);
		++n;
		sum += value;
		sumWeighted += value * weight;
		if (value < min) {
			min = value;
		}
		if (value > max) {
			max = value;
		}
		final double temp = weight + sumweight;
		final double delta = value - mean;
		final double tmp = delta * weight / temp;
		mean += tmp;
		m2 += sumweight * delta * tmp;
		sumweight = temp;
	}

	/**
	 * Unsynchronized. Only call directly when sure about it!
	 *
	 * @param othr
	 *            Another stats object to add to the current stats.
	 */
	@Override
	public void add(final IStats othr) {
		add(othr, 1);
	}

	/**
	 * Unsynchronized. Only call directly when sure about it!
	 *
	 * @param othr
	 *            Another stats object to add to the current stats.
	 */
	public void add(final IStats othr, final double weight) {
		final Stats other = (Stats) othr;
		Preconditions.checkArgument(sumweight + other.sumweight > 0);
		final double delta = other.mean - mean;
		mean = (sumweight * mean + other.sumweight * other.mean * weight) / (sumweight + other.sumweight * weight);
		m2 = m2 + other.m2 + delta * delta * n * other.n / (n + other.n);
		// TODO: not sure the weights are used correctly!
		sumweight += other.sumweight * weight;
		sumWeighted += other.sumWeighted * weight;

		n += other.n;
		sum += other.sum;
		min = Math.min(min, other.min);
		max = Math.max(max, other.max);
	}

	public void addAll(final Iterable<Double> values) {
		for (final Double value : values) {
			add(value.doubleValue());
		}
	}

	@Override
	public long getN() {
		return n;
	}

	@Override
	public double getSum() {
		return sum;
	}

	@Override
	public double getWeightedSum() {
		return sumWeighted;
	}

	@Override
	public double getMean() {
		Preconditions.checkState(n > 0);
		return mean;
	}

	@Override
	public double getMin() {
		Preconditions.checkState(n > 0);
		return min;
	}

	@Override
	public double getMax() {
		Preconditions.checkState(n > 0);
		return max;
	}

	public double variance() {
		Preconditions.checkState(n > 0);
		if (n > 1) {
			// has bias correction
			return m2 / sumweight * n / (n - 1);
		}
		return 0.0;
	}

	@Override
	public double getVariance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getStandardDeviation() {
		Preconditions.checkState(n > 0);
		if (n > 1) {
			// doesn't has full bias correction
			return Math.sqrt(m2 / sumweight * n / (n - 1));
		}
		return 0.0;
	}

	@Override
	public double getSumOfWeights() {
		return sumweight;
	}

	public double getSecondMoment() {
		return m2;
	}

	public Interval confRange95(final boolean hardMinMax) {
		return confRange(2, hardMinMax);
	}

	public Interval confRange(final double standardDeviations, final boolean hardMinMax) {
		final double std = getStandardDeviation();
		final double confMin = hardMinMax ? Math.max(min, mean - std * standardDeviations)
				: mean - std * standardDeviations;
		final double confMax = hardMinMax ? Math.min(max, mean + std * standardDeviations)
				: mean + std * standardDeviations;
		return new Interval(confMin, confMax);
	}

	public GaussianDistribution asGaussian() {
		return new GaussianDistribution(getMean(), getStandardDeviation());
	}

	/**
	 * Unsynchronized. Only call directly when sure about it!
	 */
	@Override
	public Stats copy() {
		final Stats stats = new Stats();
		stats.n = n;
		stats.sum = sum;
		stats.sumWeighted = sumWeighted;
		stats.min = min;
		stats.max = max;
		stats.mean = mean;
		stats.m2 = m2;
		stats.sumweight = sumweight;
		return stats;
	}

	@Override
	public String toString() {
		final SimpleFormatter formatter = new SimpleFormatter();
		if (getN() == 1) {
			return "{" + formatter.format(getSum()) + "," + formatter.format(getWeightedSum()) + "}";
		}
		return getN()
				+ (getN() > 0
						? ", " + formatter.format(getSum()) + ", " + formatter.format(getMean()) + " +/- "
								+ formatter.format(getStandardDeviation()) + " [" + formatter.format(getMin()) + ", "
								+ formatter.format(getMax()) + "]"
						: "");
	}
}
