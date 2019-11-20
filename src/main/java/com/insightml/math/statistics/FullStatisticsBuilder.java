package com.insightml.math.statistics;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class FullStatisticsBuilder implements StatsBuilder<FullStatistics> {

	private final DescriptiveStatistics delegate = new DescriptiveStatistics();

	@Override
	public StatsBuilder<FullStatistics> add(final double value, final double weight) {
		if (weight != 1) {
			throw new UnsupportedOperationException("We do not yet support weights other than '1'");
		}
		delegate.addValue(value);
		return this;
	}

	@Override
	public void add(final IStats stats) {
		for (final double value : ((FullStatistics) stats).values) {
			delegate.addValue(value);
		}
	}

	@Override
	public double getWeightedSum() {
		// since we do not support weighting, the weighted sum equals the simple sum
		return delegate.getSum();
	}

	@Override
	public double getSumOfWeights() {
		// since we do not support weighting, the sum of weights equals the number of
		// observation
		return delegate.getN();
	}

	@Override
	public FullStatistics create() {
		final long n = (int) delegate.getN();
		final double sum = delegate.getSum();
		final double variance = delegate.getVariance();
		final double standardDeviation = delegate.getStandardDeviation();
		final double max = delegate.getMax();
		final double min = delegate.getMin();
		final Map<Integer, Double> percentiles = new HashMap<>();
		for (int i = 5; i < 100; i += 5) {
			percentiles.put(i, delegate.getPercentile(i));
		}
		final double[] doubleValues = delegate.getValues();
		final float[] values = new float[doubleValues.length];
		// we have to create a copy to make the stats immutable, and also float precision should be enough
		for (int i = 0; i < doubleValues.length; ++i) {
			values[i] = (float) doubleValues[i];
		}
		return new FullStatistics(n, sum, variance, standardDeviation, max, min, percentiles, values);
	}

}
