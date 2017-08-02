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
package com.insightml.models;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.Sample;
import com.insightml.utils.Arrays;
import com.insightml.utils.IArguments;
import com.insightml.utils.types.collections.DoubleArray;

public abstract class AbstractBasicDoubleLearner extends AbstractDoubleLearner<Double> {

	public AbstractBasicDoubleLearner(final @Nonnull IArguments arguments) {
		super(arguments);
	}

	@Override
	public final IModel<Sample, Double> run(final LearnerInput<? extends Sample, ? extends Double> input) {
		final Pair<double[], float[][]> filtered = filter(input);
		return train(filtered.getSecond(), filtered.getFirst(), input.getTrain().featureNames());
	}

	private static Pair<double[], float[][]> filter(final LearnerInput<? extends Sample, ? extends Double> input) {
		final float[][] features = input.getTrain().features();
		final Double[] expected = input.getTrain().expected(input.labelIndex);

		final DoubleArray expFiltered = new DoubleArray(expected.length);
		final List<float[]> featsFiltered = new LinkedList<>();
		for (int i = 0; i < expected.length; ++i) {
			if (expected[i] != null) {
				expFiltered.add(expected[i].doubleValue());
				featsFiltered.add(features[i]);
			}
		}
		final float[][] featsArray = Arrays.of(featsFiltered, float[].class);
		return new Pair<>(expFiltered.toArray(), featsArray);
	}

	public abstract IModel<Sample, Double> train(float[][] features, double[] expected, String[] featureNames);

}
