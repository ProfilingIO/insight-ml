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

public final class FloatArray {

	private int i = -1;
	private final float[] arr;

	public FloatArray(final int capacity) {
		arr = new float[capacity];
	}

	public void add(final float value) {
		arr[++i] = value;
	}

	public int size() {
		return i + 1;
	}

	public float[] toArray() {
		return Arrays.copyOf(arr, size());
	}

}
