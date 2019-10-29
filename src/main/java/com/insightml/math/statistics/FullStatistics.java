package com.insightml.math.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class FullStatistics implements MutableStatistics {

	private final DescriptiveStatistics delegate;

	public FullStatistics() {
		this(new DescriptiveStatistics());
	}

	public FullStatistics(final DescriptiveStatistics delegate) {
		this.delegate = delegate;
	}

	@Override
	public double getWeightedSum() {
		// since we do not support weighting, the weighted sum equals the simple sum
		return delegate.getSum();
	}

	@Override
	public double getSumOfWeights() {
		// since we do not support weighting, the sum of weights equals the number of observation
		return getN();
	}

	@Override
	public IStats copy() {
		return new FullStatistics(new DescriptiveStatistics(delegate));
	}

	@Override
	public double getMean() {
		return delegate.getMean();
	}

	@Override
	public double getVariance() {
		return delegate.getVariance();
	}

	@Override
	public double getStandardDeviation() {
		return delegate.getStandardDeviation();
	}

	@Override
	public double getMax() {
		return delegate.getMax();
	}

	@Override
	public double getMin() {
		return delegate.getMin();
	}

	@Override
	public long getN() {
		return delegate.getN();
	}

	@Override
	public double getSum() {
		return delegate.getSum();
	}

	@Override
	public void add(final double value, final double weight) {
		if (weight != 1) {
			throw new UnsupportedOperationException("We do not yet support weights other than '1'");
		}
		delegate.addValue(value);
	}

	@Override
	public void add(final IStats stats) {
		for (final double value : ((FullStatistics) stats).delegate.getValues()) {
			delegate.addValue(value);
		}
	}

}
