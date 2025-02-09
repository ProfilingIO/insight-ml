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
package com.insightml.evaluation.functions;

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.ISamples;
import com.insightml.utils.Check;
import com.insightml.utils.types.collections.DoubleLinkedList;

public final class AUC extends AbstractIndependentLabelsObjectiveFunction<Object, Number> {

	private static final long serialVersionUID = -3143531378886930734L;

	@Override
	public String getDescription() {
		return "Area under receiver operating characteristic (ROC) curve";
	}

	@Override
	public DescriptiveStatistics label(final Number[] preds, final Object[] expected, final double[] weights,
			final ISamples<?, ?> samples, final int labelIndex) {
		final DoubleLinkedList[] scores = { new DoubleLinkedList(), new DoubleLinkedList() };
		for (int i = 0; i < preds.length; ++i) {
			final Object orig = expected[i];
			final boolean label = orig instanceof Boolean ? (Boolean) orig
					// we normally expect to be between 0 (false) and 1 (true). There might be some cases that have a
					// bit of a different scoring/weighting, so we allow scores to be outside the range. But we can't
					// allow -1 to 1 or 0 to 2, as it's then unclear whether > 0 is already "true" or only > 1 is "true"
					: Check.num(((Number) orig).doubleValue(), -0.9, 1.9) >= 0.5;
			scores[label ? 1 : 0].add((Double) preds[i]);
		}
		return new DescriptiveStatistics(new double[] { auc(scores) });
	}

	static double auc(final DoubleLinkedList[] scores) {
		final double[] arr0 = scores[0].toArray();
		final double[] arr1 = scores[1].toArray();

		Arrays.sort(arr0);
		Arrays.sort(arr1);

		final double n0 = arr0.length;
		final double n1 = arr1.length;

		if (n0 == 0 || n1 == 0) {
			return 0.5;
		}

		// scan the data
		int i0 = 0;
		int i1 = 0;
		int rank = 1;
		double rankSum = 0;
		while (i0 < n0 && i1 < n1) {

			final double v0 = arr0[i0];
			final double v1 = arr1[i1];

			if (v0 < v1) {
				++i0;
				++rank;
			} else if (v1 < v0) {
				i1++;
				rankSum += rank;
				rank++;
			} else {
				// ties have to be handled delicately
				final double tieScore = v0;

				// how many negatives are tied?
				int k0 = 0;
				while (i0 < n0 && arr0[i0] == tieScore) {
					++k0;
					++i0;
				}

				// and how many positives
				int k1 = 0;
				while (i1 < n1 && arr1[i1] == tieScore) {
					++k1;
					++i1;
				}

				// we found k0 + k1 tied values which have
				// ranks in the half open interval [rank, rank + k0 + k1)
				// the average rank is assigned to all
				rankSum += (rank + (k0 + k1 - 1) / 2.0) * k1;
				rank += k0 + k1;
			}
		}

		if (i1 < n1) {
			rankSum += (rank + (n1 - i1 - 1) / 2.0) * (n1 - i1);
			rank += (int) (n1 - i1);
		}

		return (rankSum / n1 - (n1 + 1) / 2) / n0;
	}

}
