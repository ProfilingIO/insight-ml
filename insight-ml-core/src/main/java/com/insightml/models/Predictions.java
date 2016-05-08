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

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Samples;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

public class Predictions<E, P> extends AbstractClass implements Serializable {

	private static final long serialVersionUID = -8985419504038095904L;

	private int run;
	private int labelIndex;
	private P[] predictions;
	private E[] expected;
	private double[] weights;
	private transient ISamples<? extends ISample, E> samples;

	protected Predictions() {
	}

	public <I extends ISample> Predictions(final int run, final ModelPipeline<I, P> model,
			final Iterable<? extends I> samples) {
		this.run = run;
		this.labelIndex = model.getLabelIndex();
		predictions = model.run(samples);
		final Samples<? extends I, E> instances = new Samples<>(samples);
		Check.size(predictions, 1, instances.size());
		expected = instances.expected(labelIndex);
		weights = instances.weights(labelIndex);
		this.samples = instances;
		model.close();
	}

	public P[] getPredictions() {
		return predictions;
	}

	public ISamples<? extends ISample, E> getSamples() {
		return samples;
	}

	public ISample getSample(final int i) {
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

	public List<Pair<ISample, P>> asList() {
		final List<Pair<ISample, P>> list = new LinkedList<>();
		for (int i = 0; i < size(); ++i) {
			list.add(new Pair<ISample, P>(samples.get(i), predictions[i]));
		}
		return list;
	}

	@Override
	public String toString() {
		return "Preds{" + run + ", " + getLabelIndex() + ", " + size() + "}";
	}

}
