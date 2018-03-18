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

import com.insightml.utils.types.AbstractClass;

public abstract class AbstractSample<E> extends AbstractClass implements Sample, Cloneable {

	private static final long serialVersionUID = -7239560442248089807L;

	private int id;
	private E[] expected;
	private float weight;

	protected AbstractSample() {
	}

	protected AbstractSample(final int id, final E[] expected) {
		this(id, expected, 1.0f);
	}

	protected AbstractSample(final int id, final E[] expected, final float weight) {
		this.id = id;
		this.expected = expected;
		this.weight = weight;
	}

	@Override
	public final int getId() {
		return id;
	}

	@Override
	public final E[] getExpected() {
		return expected;
	}

	@Override
	public final E getExpected(final int index) {
		return expected == null ? null : expected[index];
	}

	@Override
	public float getWeight(final int labelIndex) {
		return weight;
	}

	@Override
	public String getComment() {
		return null;
	}

	@Override
	public void writeInfo(final ISampleInfoBuilder builder, final Iterable<? extends Sample> instances) {
		throw new IllegalAccessError();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Sample)) {
			throw new IllegalStateException("Tried to compare " + this + " to " + o + " (" + o.getClass() + ")");
		}
		return getId() == ((Sample) o).getId();
	}

	@Override
	public int compareTo(final Sample o) {
		return Integer.compare(getId(), o.getId());
	}

}
