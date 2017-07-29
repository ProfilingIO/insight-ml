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

import java.util.Random;

import com.google.common.base.Preconditions;
import com.insightml.data.samples.AbstractSamples;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;

public abstract class AbstractDecorator<S extends Sample, E> extends AbstractSamples<S, E> {
	private static final long serialVersionUID = -806564214146792007L;

	protected final ISamples<S, E> ref;

	public AbstractDecorator(final ISamples<S, E> orig) {
		this.ref = Preconditions.checkNotNull(orig);
	}

	protected abstract int getInstance(int i);

	@Override
	public final S get(final int i) {
		return ref.get(getInstance(i));
	}

	@Override
	public final int getId(final int i) {
		return ref.getId(getInstance(i));
	}

	@Override
	public final int numLabels() {
		return ref.numLabels();
	}

	@Override
	public final SamplesMapping<S, E> randomize(final Random random) {
		throw new IllegalAccessError(this + "");
	}

}
