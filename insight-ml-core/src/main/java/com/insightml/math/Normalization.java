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
package com.insightml.math;

import com.insightml.math.statistics.Stats;

public enum Normalization {

	LINEAR, LOG, QUAD;

	private INormalizer normalizer;

	INormalizer run(final double[] values) {
		return normalizer;
	}

	public interface INormalizer {

		double transform(double value);

	}

	static double linear(final double value, final double[] values) {
		final Stats stats = new Stats(values);
		return (value - stats.getMin()) / (stats.getMax() - stats.getMin());
	}

	static double log(final double value, final double[] values) {
		final Stats stats = new Stats(values);
		return (Math.log(value) - Math.log(stats.getMin())) / (Math.log(stats.getMax()) - Math.log(stats.getMin()));
	}

	static double quad(final double value, final double[] values) {
		final Stats stats = new Stats(values);
		return Math.pow((value - stats.getMin()) / (stats.getMax() - stats.getMin()), 2);
	}
}
