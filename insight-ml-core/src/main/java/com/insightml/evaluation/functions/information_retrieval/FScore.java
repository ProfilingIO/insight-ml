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

import java.util.Collection;
import java.util.List;

import com.insightml.data.samples.Sample;
import com.insightml.math.Maths;

public final class FScore<E> extends AbstractIRFunction<E, E> {

	private static final long serialVersionUID = 6752504522625579581L;

	private final AbstractIRFunction<E, E> precision;
	private final AbstractIRFunction<E, E> recall;
	private final double beta;
	private final String name;

	public FScore(final AbstractIRFunction<E, E> precision, final AbstractIRFunction<E, E> recall) {
		this(precision, recall, 1.0, "F1");
	}

	public FScore(final AbstractIRFunction<E, E> precision, final AbstractIRFunction<E, E> recall, final double weight,
			final String name) {
		super(precision.isMacro());
		this.precision = precision;
		this.recall = recall;
		this.beta = weight;
		this.name = name;
	}

	@Override
	protected String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return "2*precision*recall/(precision+recall)";
	}

	@Override
	protected double micro(final Collection<? extends E>[] preds, final List<? extends E[]> expected) {
		return Maths.fScore(precision.micro(preds, expected), recall.micro(preds, expected), beta);
	}

	@Override
	public double instance(final Collection<? extends E> pred, final E[] expected, final Sample sample,
			final int labelIndex) {
		return Maths.fScore(precision.instance(pred, expected, sample, labelIndex),
				recall.instance(pred, expected, sample, labelIndex), beta);
	}

}
