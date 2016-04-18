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
package com.insightml.data.samples.decorators;

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.utils.Check;

public final class LabelDecorator<S extends ISample, E> extends AbstractDecorator<S, E> {

	private final E[] expected;
	private final int label;

	public LabelDecorator(final ISamples<S, E> orig, final E[] expected, final int labelIndex) {
		super(orig);
		Check.equals(size(), expected.length);
		this.expected = expected;
		this.label = labelIndex;
		for (final E element : expected) {
			Check.notNull(element);
		}
	}

	@Override
	protected int getInstance(final int i) {
		return i;
	}

	@Override
	public int size() {
		return ref.size();
	}

	@Override
	public E[] expected(final int labelIndex) {
		Check.equals(labelIndex, label);
		return expected;
	}

	@Override
	public double[] weights(final int labelIndex) {
		return ref.weights(labelIndex);
	}

	@Override
	public int numFeatures() {
		return ref.numFeatures();
	}

	@Override
	public String[] featureNames() {
		return ref.featureNames();
	}

	@Override
	public double[][] features() {
		return ref.features();
	}

	@Override
	public int[][] orderedIndexes() {
		return ref.orderedIndexes();
	}

}
