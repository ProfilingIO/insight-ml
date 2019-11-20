package com.insightml.math.statistics;

public interface StatsBuilder<S extends IStats> {

	StatsBuilder<S> add(double value, double weight);

	void add(IStats stats);

	double getWeightedSum();

	double getSumOfWeights();

	/**
	 * @return the statistics object built up to this point. Subsequent changes to the builder shall not change the
	 *         statistics object returned here; ideally the stats itself are immutable
	 */
	S create();

}
