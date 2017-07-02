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
package com.insightml.evaluation.functions.information_retrieval;

import java.util.List;

import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.AbstractObjectiveFunctionFrame;
import com.insightml.utils.Arrays;

public final class MeanAveragePrecision<E> extends AbstractObjectiveFunctionFrame<E[], List<E>> {

	private static final long serialVersionUID = -4580795835175154686L;

	private final int max;

	public MeanAveragePrecision(final int max) {
		this.max = max;
	}

	@Override
	public String getName() {
		return "MAP@" + max;
	}

	@Override
	public String getDescription() {
		return "Mean average precision of the first " + max + " elements.";
	}

	@Override
	public double instance(final List<E> prediction, final E[] expected, final Sample sample, final int labelIndex) {
		final int n = Math.min(max, prediction.size());
		double sum = 0;
		for (int i = 0; i < n; ++i) {
			if (Arrays.contains(expected, prediction.get(i))) {
				sum += new Precision(false).instance(prediction.subList(0, i + 1), expected, sample, labelIndex);
			}
		}
		// Math.min(max, expected.length);
		return sum == 0 ? 0 : sum / expected.length;
	}
}
