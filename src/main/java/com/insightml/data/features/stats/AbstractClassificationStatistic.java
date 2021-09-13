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
package com.insightml.data.features.stats;

import com.insightml.math.Vectors;

public abstract class AbstractClassificationStatistic extends AbstractIndependentFeatureStatistic {

	private final double binSize;

	protected AbstractClassificationStatistic(final double binSize) {
		this.binSize = binSize;
	}

	@Override
	protected final double compute(final FeatureStatistics stats, final int feature, final String featureName) {
		final double min = stats.getMin(featureName);
		final ClassFeatureMatrix matrix = new ClassFeatureMatrix(
				(int) Math.floor((stats.getMax(featureName) - min) / binSize + 1));
		final float[][] features = stats.getInstances().features();
		final Object[] expected = stats.getInstances().expected(stats.getLabelIndex());
		for (int i = 0; i < stats.getInstances().size(); ++i) {
			final int bin = calculateBin(features[i][feature], min);
			final int label = expected[i] instanceof Boolean ? (Boolean) expected[i] ? 1 : 0
					: ((Number) expected[i]).intValue();
			matrix.addPair(label, bin);
		}
		return calculate(matrix);
	}

	private int calculateBin(final double feature, final double min) {
		return (int) Math.floor((feature - min) / binSize);
	}

	private double calculate(final ClassFeatureMatrix matrix) {
		double sum = 0;
		for (int labelBin = 0; labelBin <= 1; ++labelBin) {
			for (int featureBin = 0; featureBin < matrix.numBins(); ++featureBin) {
				sum += pointWise(matrix, labelBin, featureBin);
			}
		}
		return sum;
	}

	protected abstract double pointWise(final ClassFeatureMatrix matrix, final int labelBin, final int featureBin);

	protected static final class ClassFeatureMatrix {

		private final int[][] counts;
		private int n;

		ClassFeatureMatrix(final int numFeatureBins) {
			counts = new int[2][numFeatureBins];
		}

		void addPair(final int labelBin, final int featureBin) {
			++counts[labelBin][featureBin];
			++n;
		}

		int counts(final int labelBin, final int featureBin) {
			return counts[labelBin][featureBin];
		}

		double prob(final int labelBin, final int featureBin) {
			return counts[labelBin][featureBin] * 1.0 / n;
		}

		int classCount(final int labelBin) {
			return Vectors.sum(counts[labelBin]);
		}

		double classProb(final int labelBin) {
			return classCount(labelBin) * 1.0 / n;
		}

		int featureCount(final int featureBin) {
			int sum = 0;
			for (final int[] label : counts) {
				sum += label[featureBin];
			}
			return sum;
		}

		double featureProb(final int featureBin) {
			return featureCount(featureBin) * 1.0 / n;
		}

		public int getN() {
			return n;
		}

		int numBins() {
			return counts[0].length;
		}

	}

}
