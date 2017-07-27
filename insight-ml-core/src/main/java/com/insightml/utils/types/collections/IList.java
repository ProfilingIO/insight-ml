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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class IList<E> extends DefensiveList<E> implements Serializable {

	private static final long serialVersionUID = 2319946981077893168L;

	final List<E> list;

	public IList() {
		list = new LinkedList<>();
	}

	public IList(final @Nonnull E element) {
		this.list = ImmutableList.of(element);
	}

	public IList(final @Nonnull Iterable<E> list) {
		this.list = Lists.newLinkedList(list);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean contains(final Object o) {
		return list.contains(o);
	}

	@Override
	public E get(final int index) {
		return list.get(index);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return list.toArray(a);
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private final Iterator<E> it = list.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public E next() {
				return it.next();
			}

			@Override
			public void remove() {
				throw new IllegalAccessError();
			}
		};
	}

	@Override
	public String toString() {
		return list.toString();
	}

}
