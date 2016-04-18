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
package com.insightml.math.types;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.insightml.utils.types.AbstractClass;

public final class Interval extends AbstractClass implements Serializable {

	private static final long serialVersionUID = 1481989855779409012L;

	private final double a;
	private final double b;

	public Interval(final double a, final double b) {
		Preconditions.checkArgument(b >= a);
		this.a = a;
		this.b = b;
	}

	public double getStart() {
		return a;
	}

	public double getEnd() {
		return b;
	}

	public double getLength() {
		return b - a;
	}

	public boolean intersects(final Interval interval) {
		return interval.a >= a && interval.a <= b || interval.b >= a && interval.b <= b;
	}

	public boolean contains(final double v) {
		return v >= a && v <= b;
	}

	public double relativePosition(final double value) {
		final double rangeMax = getEnd();
		final double rangeMin = getStart();
		if (value >= rangeMax) {
			return 1;
		}
		if (value <= rangeMin) {
			return 0;
		}
		return (value - rangeMin) / (rangeMax - rangeMin);
	}

	public double[] toArray() {
		return new double[] { a, b };
	}

	@Override
	public String toString() {
		return "[" + a + "," + b + "]";
	}

}
