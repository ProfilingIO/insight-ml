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

import java.util.Iterator;

public final class ArrayIterator<T> implements Iterator<T>, Iterable<T> {

    private final T[] array;
    private int index = -1;

    public ArrayIterator(final T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index + 1 < array.length;
    }

    @Override
    public T next() {
        return array[++index];
    }

    @Override
    public void remove() {
        throw new IllegalAccessError();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

}
