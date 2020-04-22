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
