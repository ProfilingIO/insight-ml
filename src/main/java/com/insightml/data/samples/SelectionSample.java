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
package com.insightml.data.samples;

import com.insightml.utils.Check;

public final class SelectionSample<E, R> extends AbstractSample<Double> {

	private static final long serialVersionUID = -4313877491043011982L;

	private final int position;
	private final E element;
	private final R reference;

	public SelectionSample(final int id, final E element, final R reference, final double score, final float weight) {
		super(id, new Double[] { score }, weight);
		this.position = Check.num(id, 1, 9999);
		this.element = element;
		this.reference = Check.notNull(reference);
	}

	public E getElement() {
		return element;
	}

	public int getElementPosition() {
		return position;
	}

	public R getReference() {
		return Check.notNull(reference);
	}

	@Override
	public String toString() {
		return "SelectionItem{" + element + ", " + getExpected(0) + "}";
	}

}
