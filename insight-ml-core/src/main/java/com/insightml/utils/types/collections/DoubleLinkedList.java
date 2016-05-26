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

public final class DoubleLinkedList {
	private Node tail;
	private int size;

	public void add(final double d) {
		final Node node = new Node(d);
		node.before = tail;
		tail = node;
		++size;
	}

	public double[] toArray() {
		final double[] arr = new double[size];
		int i = size;
		for (Node pointer = tail; pointer != null; pointer = pointer.before) {
			arr[--i] = pointer.value;
		}
		return arr;
	}

	private static final class Node {

		Node before;
		final double value;

		Node(final double value) {
			this.value = value;
		}
	}

}
