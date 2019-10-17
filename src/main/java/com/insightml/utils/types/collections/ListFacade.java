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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.insightml.utils.Check;

public class ListFacade<E> extends DefensiveList<E> implements Serializable, KryoSerializable {

	private static final long serialVersionUID = 7555602477842326527L;

	private List<E> list;

	public ListFacade(final List<E> list) {
		this.list = list;
	}

	@Override
	public final void write(final Kryo kryo, final Output output) {
		kryo.writeObject(output, list);
	}

	@Override
	public final void read(final Kryo kryo, final Input input) {
		list = kryo.readObject(input, LinkedList.class);
	}

	protected final List<E> getList() {
		return list;
	}

	@Override
	public boolean add(final E e) {
		return Check.state(list.add(e));
	}

	@Override
	public final boolean contains(final Object o) {
		return list.contains(o);
	}

	@Override
	public final E get(final int index) {
		return list.get(index);
	}

	@Override
	public boolean remove(final Object o) {
		return list.remove(o);
	}

	@Override
	public final Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public final int size() {
		return list.size();
	}

	@Override
	public final boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public final List<E> subList(final int fromIndex, final int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public final Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return list.toArray(a);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

}
