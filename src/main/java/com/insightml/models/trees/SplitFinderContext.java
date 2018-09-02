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
package com.insightml.models.trees;

import javax.annotation.Nullable;

import com.insightml.data.samples.ISamples;
import com.insightml.utils.Utils;

public final class SplitFinderContext {

	public final double[] expected;
	public final double[] weights;
	public final String[] featureNames;
	public final double[][] features;
	public final int[][] orderedInstances;

	@Nullable
	final boolean[] featuresMask;

	public final int maxDepth;
	double minImprovement;
	final int labelIndex;

	public SplitFinderContext(final ISamples<?, Double> instances, @Nullable final boolean[] featuresMask,
			final int maxDepth, final double minImprovement, final int labelIndex) {
		final Object[] exp = instances.expected(labelIndex);
		expected = new double[exp.length];
		for (int i = 0; i < exp.length; ++i) {
			expected[i] = Utils.toDouble(exp[i]);
		}
		weights = instances.weights(labelIndex);
		featureNames = instances.featureNames();
		features = instances.features();
		orderedInstances = instances.orderedIndexes();

		this.featuresMask = featuresMask;

		this.maxDepth = maxDepth;
		this.minImprovement = minImprovement;
		this.labelIndex = labelIndex;
	}

}
