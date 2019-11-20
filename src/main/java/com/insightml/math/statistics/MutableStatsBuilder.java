package com.insightml.math.statistics;

public class MutableStatsBuilder<S extends MutableStatistics> implements StatsBuilder<S> {

	private final S stats;

	public MutableStatsBuilder(final S stats) {
		this.stats = stats;
	}

	@Override
	public MutableStatsBuilder<S> add(final double value, final double weight) {
		stats.add(value, weight);
		return this;
	}

	@Override
	public void add(final IStats other) {
		stats.add(other);
	}

	@Override
	public double getWeightedSum() {
		return stats.getWeightedSum();
	}

	@Override
	public double getSumOfWeights() {
		return stats.getSumOfWeights();
	}

	@Override
	public S create() {
		// TODO: create a simplified, immutable version
		return (S) stats.copy();
	}

}
