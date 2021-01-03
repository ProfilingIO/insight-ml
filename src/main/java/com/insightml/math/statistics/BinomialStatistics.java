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

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.apache.commons.math3.stat.interval.IntervalUtils;

/**
 * For weighted trials, variance and confidence statistics aren't entirely accurate, as we don't store single
 * observations and instead treat the sum of weights as the number of trials. Incremental algorithm for better estimates
 * of weighted statistics to be added later
 */
public class BinomialStatistics implements MutableStatistics {
	private long trials;
	private double weightedTrials;
	private long successes;
	private double weightedSuccesses;

	public BinomialStatistics() {
	}

	public BinomialStatistics(final long trials, final long successes) {
		this(trials, trials, successes, successes);
	}

	public BinomialStatistics(final long trials, final double weightedTrials, final long successes,
			final double weightedSuccesses) {
		this.trials = trials;
		this.weightedTrials = weightedTrials;
		this.successes = successes;
		this.weightedSuccesses = weightedSuccesses;
	}

	@Override
	public long getN() {
		return trials;
	}

	@Override
	public double getMean() {
		return weightedSuccesses * 1.0 / weightedTrials;
	}

	@Override
	public double getVariance() {
		final double probability = getMean();
		return weightedTrials * probability * (1 - probability);
	}

	@Override
	public double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}

	@Override
	public double getMax() {
		return successes > 0 ? 1 : 0;
	}

	@Override
	public double getMin() {
		return successes < trials ? 0 : 1;
	}

	@Override
	public double getSum() {
		return successes;
	}

	@Override
	public double getWeightedSum() {
		return weightedSuccesses;
	}

	@Override
	public double getSumOfWeights() {
		return weightedTrials;
	}

	public double getStandardError() {
		final double probability = getMean();
		return Math.sqrt(probability * (1 - probability) / weightedTrials);
	}

	@Nullable
	public ConfidenceInterval getSuccessProbabilityConfidenceInterval() {
		return successes > 0 ? getSuccessProbabilityConfidenceInterval(0.95) : null;
	}

	public ConfidenceInterval getSuccessProbabilityConfidenceInterval(final double confidenceLevel) {
		return IntervalUtils
				.getNormalApproximationInterval((int) weightedTrials, (int) weightedSuccesses, confidenceLevel);
	}

	public double sampleSuccessProbability(final Random random) {
		return getMean() + random.nextGaussian() * getStandardError();
	}

	@Override
	public void add(final double value, final double weight) {
		++trials;
		weightedTrials += weight;

		if ((float) value == 1) {
			++successes;
			weightedSuccesses += weight;
		} else if ((float) value != 0) {
			throw new IllegalArgumentException("Can only handle values '0' and '1'");
		}
	}

	@Override
	public void add(final IStats stats) {
		checkIfBinomial(stats);
		trials += stats.getN();
		weightedTrials += stats.getSumOfWeights();
		successes += stats.getSum();
		weightedSuccesses += stats.getWeightedSum();
	}

	public void add(final IStats stats, final double weight) {
		checkIfBinomial(stats);
		trials += stats.getN();
		weightedTrials += stats.getSumOfWeights() * weight;
		successes += stats.getSum();
		weightedSuccesses += stats.getWeightedSum() * weight;
	}

	private static void checkIfBinomial(final IStats stats) {
		final double min = stats.getMin();
		if (min != 0 && min != 1) {
			throw new IllegalArgumentException("Binomial distribution expects only values of 0 and 1");
		}
		final double max = stats.getMax();
		if (max != 0 && max != 1) {
			throw new IllegalArgumentException("Binomial distribution expects only values of 0 and 1");
		}
	}

	@Override
	public IStats copy() {
		return new BinomialStatistics(trials, weightedTrials, successes, weightedSuccesses);
	}

}
