package com.insightml.math.statistics;

import java.util.Map;

public class FullStatistics implements IStats {

	private final long n;
	private final double sum;
	private final double variance;
	private final double standardDeviation;
	private final double max;
	private final double min;
	private final Map<Integer, Double> percentiles;

	// Still required to merge two instances of FullStatistics
	final float[] values;

	public FullStatistics(final long n, final double sum, final double variance, final double standardDeviation,
			final double max, final double min, final Map<Integer, Double> percentiles, final float[] values) {
		this.n = n;
		this.sum = sum;
		this.variance = variance;
		this.standardDeviation = standardDeviation;
		this.max = max;
		this.min = min;
		this.percentiles = percentiles;
		this.values = values;
	}

	@Override
	public double getWeightedSum() {
		// since we do not support weighting, the weighted sum equals the simple sum
		return sum;
	}

	@Override
	public double getSumOfWeights() {
		// since we do not support weighting, the sum of weights equals the number of
		// observation
		return getN();
	}

	@Override
	public IStats copy() {
		// It's immutable, so why should we copy
		return this;
	}

	@Override
	public double getMean() {
		return sum / n;
	}

	@Override
	public double getVariance() {
		return variance;
	}

	@Override
	public double getStandardDeviation() {
		return standardDeviation;
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public double getMin() {
		return min;
	}

	@Override
	public long getN() {
		return n;
	}

	@Override
	public double getSum() {
		return sum;
	}

	public double getPercentile(final int percentile) {
		final Double value = percentiles.get(percentile);
		if (value == null) {
			throw new UnsupportedOperationException();
		}
		return value;
	}

}
