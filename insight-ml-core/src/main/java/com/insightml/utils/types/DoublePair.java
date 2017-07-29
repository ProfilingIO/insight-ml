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
package com.insightml.utils.types;

import java.io.Serializable;

public final class DoublePair<T> extends AbstractClass implements Serializable {

	private static final long serialVersionUID = 850618188901102118L;

	private T first;
	private double second;

	DoublePair() {
	}

	public DoublePair(final T first, final double second) {
		this.first = first;
		this.second = second;
		if (Double.isNaN(second)) {
			throw new IllegalStateException(first + "");
		}
	}

	public T getKey() {
		return first;
	}

	public double getValue() {
		return second;
	}

	public int compareDouble(final DoublePair<?> pair) {
		return Double.compare(second, pair.second);
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}
