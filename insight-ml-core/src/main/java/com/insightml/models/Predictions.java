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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.Samples;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

public class Predictions<E, P> extends AbstractClass implements Serializable {

	private static final long serialVersionUID = -8985419504038095904L;

	private final int run;
	private final int labelIndex;
	private final P[] predictions;
	private final E[] expected;
	private final double[] weights;
	private transient ISamples<? extends Sample, E> samples;
	private final int timeInMillis;
	private final int modelTrainingTimeInMillis;

	public Predictions(final int run, final int labelIndex, final P[] predictions, final E[] expected,
			final double[] weights, final ISamples<? extends Sample, E> samples, final int timeInMillis,
			final int modelTrainingTimeInMillis) {
		this.run = run;
		this.labelIndex = labelIndex;
		this.predictions = predictions;
		this.expected = expected;
		this.weights = weights;
		this.samples = samples;
		this.timeInMillis = timeInMillis;
		this.modelTrainingTimeInMillis = modelTrainingTimeInMillis;
	}

	public static <I extends Sample, E, P> Predictions<E, P> create(final int run, final ModelPipeline<I, P> model,
			final Iterable<? extends I> samples, final int modelTrainingTimeInMillis) {
		final long start = System.currentTimeMillis();
		final int labelIndex = model.getLabelIndex();
		final P[] predictions = model.run(samples);
		final Samples<? extends I, E> instances = new Samples<>(samples);
		Check.size(predictions, 1, instances.size());
		final E[] expected = instances.expected(labelIndex);
		final double[] weights = instances.weights(labelIndex);
		model.close();
		final int duration = (int) (System.currentTimeMillis() - start);
		return new Predictions<>(run, labelIndex, predictions, expected, weights, instances, duration,
				modelTrainingTimeInMillis);
	}

	public P[] getPredictions() {
		return predictions;
	}

	public ISamples<? extends Sample, E> getSamples() {
		return samples;
	}

	public Sample getSample(final int i) {
		return samples.get(i);
	}

	public E[] getExpected() {
		return expected;
	}

	public double[] getWeights() {
		return weights;
	}

	public int getSet() {
		return Check.num(run, 1, 9999);
	}

	public int getLabelIndex() {
		return labelIndex;
	}

	public int size() {
		return predictions.length;
	}

	public int getTimeInMillis() {
		return timeInMillis;
	}

	public int getModelTrainingTimeInMillis() {
		return modelTrainingTimeInMillis;
	}

	public List<Pair<Sample, P>> asList() {
		final List<Pair<Sample, P>> list = new LinkedList<>();
		for (int i = 0; i < size(); ++i) {
			list.add(new Pair<Sample, P>(samples.get(i), predictions[i]));
		}
		return list;
	}

	@Override
	public String toString() {
		return "Preds{" + run + ", " + getLabelIndex() + ", " + size() + "}";
	}

}
