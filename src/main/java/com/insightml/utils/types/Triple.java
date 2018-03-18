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

public final class Triple<E, F, G> extends AbstractClass {

	private final E first;
	private F second;
	private G third;

	public Triple(final E first, final F second, final G third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public static <E, F, G> Triple<E, F, G> create(final E first, final F second, final G third) {
		return new Triple<>(first, second, third);
	}

	public E getFirst() {
		return this.first;
	}

	public F getSecond() {
		return this.second;
	}

	public void setSecond(final F value) {
		this.second = value;
	}

	public G getThird() {
		return this.third;
	}

	public void setThird(final G value) {
		third = value;
	}

	@Override
	public String toString() {
		return "(" + this.first + "," + this.second + "," + this.third + ")";
	}

}
