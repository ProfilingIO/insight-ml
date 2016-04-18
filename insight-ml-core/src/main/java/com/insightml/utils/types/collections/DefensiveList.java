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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.insightml.utils.types.AbstractClass;

public class DefensiveList<E> extends AbstractClass implements List<E> {

    @Override
    public boolean add(final E e) {
        throw new IllegalAccessError(getClass().toString());
    }

    @Override
    public final void add(final int index, final E element) {
        throw new IllegalAccessError();
    }

    @Override
    public final boolean addAll(final Collection<? extends E> c) {
        throw new IllegalAccessError();
    }

    @Override
    public final boolean addAll(final int index, final Collection<? extends E> c) {
        throw new IllegalAccessError();
    }

    @Override
    public final void clear() {
        throw new IllegalAccessError();
    }

    @Override
    public boolean contains(final Object o) {
        throw new IllegalAccessError();
    }

    @Override
    public final boolean containsAll(final Collection<?> c) {
        throw new IllegalAccessError();
    }

    @Override
    public E get(final int index) {
        throw new IllegalAccessError();
    }

    @Override
    public final int indexOf(final Object o) {
        throw new IllegalAccessError();
    }

    @Override
    public boolean isEmpty() {
        throw new IllegalAccessError();
    }

    @Override
    public Iterator<E> iterator() {
        throw new IllegalAccessError();
    }

    @Override
    public final int lastIndexOf(final Object o) {
        throw new IllegalAccessError();
    }

    @Override
    public final ListIterator<E> listIterator() {
        throw new IllegalAccessError();
    }

    @Override
    public final ListIterator<E> listIterator(final int index) {
        throw new IllegalAccessError();
    }

    @Override
    public boolean remove(final Object o) {
        throw new IllegalAccessError();
    }

    @Override
    public final E remove(final int index) {
        throw new IllegalAccessError();
    }

    @Override
    public final boolean removeAll(final Collection<?> c) {
        throw new IllegalAccessError();
    }

    @Override
    public final boolean retainAll(final Collection<?> c) {
        throw new IllegalAccessError();
    }

    @Override
    public final E set(final int index, final E element) {
        throw new IllegalAccessError();
    }

    @Override
    public int size() {
        throw new IllegalAccessError();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        throw new IllegalAccessError();
    }

    @Override
    public Object[] toArray() {
        throw new IllegalAccessError();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new IllegalAccessError(getClass().toString());
    }

}
