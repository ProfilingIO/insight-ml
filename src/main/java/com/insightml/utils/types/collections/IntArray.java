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
package com.insightml.utils.types.collections;

import java.util.Arrays;

import javax.annotation.Nonnull;

public final class IntArray {

	private int i = -1;
	private final int[] arr;

	public IntArray(final int capacity) {
		arr = new int[capacity];
	}

	public void add(final int value) {
		arr[++i] = value;
	}

	public int size() {
		return i + 1;
	}

	@SuppressWarnings("null")
	@Nonnull
	public int[] toArray() {
		return Arrays.copyOf(arr, i + 1);
	}

}
