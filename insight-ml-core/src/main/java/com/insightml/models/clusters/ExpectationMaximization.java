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
package com.insightml.models.clusters;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.insightml.math.Vectors;

public abstract class ExpectationMaximization<P, D extends Serializable> {

	@Nonnull
	public final ExpectationMaximizationResult<D> run(final P[] data, final int components, final int iterations) {
		final D[] comps = init(data, components);
		final double[] compWeights = Vectors.fill(1.0 / components, components);
		final double[][] data2components = new double[data.length][components];
		for (int i = 0; i < iterations; ++i) {
			expectation(data, data2components, comps, compWeights);
			for (int c = 0; c < comps.length; ++c) {
				comps[c] = maximization(data, data2components, c);
				double softCount = 0;
				for (final double[] data2component : data2components) {
					if (data2component[c] != 0) {
						softCount += data2component[c];
					}
				}
				compWeights[c] = softCount / data.length;
			}
		}
		return new ExpectationMaximizationResult<>(comps, compWeights, data2components);
	}

	protected abstract D[] init(P[] data, int numComponents);

	private void expectation(final P[] data, final double[][] data2components, final D[] comps,
			final double[] compWeights) {
		final int components = comps.length;
		for (int i = 0; i < data2components.length; ++i) {
			double sum = 0;
			for (int j = 0; j < components; ++j) {
				final double prob = compWeights[j] * likelihood(data[i], comps[j]);
				data2components[i][j] = prob;
				sum += prob;
			}
			// make distribution
			for (int j = 0; j < components; ++j) {
				data2components[i][j] /= sum;
			}
		}
	}

	protected abstract double likelihood(P point, D dist);

	protected abstract D maximization(final P[] data, final double[][] points, final int comp);

	public static final class ExpectationMaximizationResult<D extends Serializable> implements Serializable {
		private static final long serialVersionUID = -6315456093852760979L;

		public final D[] comps;
		public final double[] compWeights;
		public transient final double[][] data2components;

		public ExpectationMaximizationResult(final D[] comps, final double[] compWeights,
				final double[][] data2components) {
			this.comps = comps;
			this.compWeights = compWeights;
			this.data2components = data2components;
		}
	}

}
