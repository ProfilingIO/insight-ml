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

public class SimpleStatistics implements MutableStatistics, Serializable {
	private static final long serialVersionUID = 8907182889325610117L;

	private long n;
	private double sum;
	private double weightedSum;
	private double sumOfWeights;

	public SimpleStatistics() {
	}

	public SimpleStatistics(final long n, final double sum, final double weightedSum, final double sumOfWeights) {
		this.n = n;
		this.sum = sum;
		this.weightedSum = weightedSum;
		this.sumOfWeights = sumOfWeights;
	}

	@Override
	public void add(final double value, final double weight) {
		++n;
		sum += value;
		weightedSum += value * weight;
		sumOfWeights += weight;
	}

	@Override
	public void add(final IStats stats) {
		n += stats.getN();
		sum += stats.getSum();
		weightedSum += stats.getWeightedSum();
		sumOfWeights += stats.getSumOfWeights();
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
	public double getMean() {
		if (sumOfWeights == 0) {
			throw new IllegalStateException();
		}
		return weightedSum / sumOfWeights;
	}

	@Override
	public double getWeightedSum() {
		return weightedSum;
	}

	@Override
	public double getSumOfWeights() {
		return sumOfWeights;
	}

	@Override
	public double getMax() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getMin() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getVariance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getStandardDeviation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IStats copy() {
		return new SimpleStatistics(n, sum, weightedSum, sumOfWeights);
	}

}
