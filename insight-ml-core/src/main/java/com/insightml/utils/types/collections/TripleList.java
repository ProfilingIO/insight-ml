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
import java.util.LinkedList;
import java.util.List;

import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.Triple;

public final class TripleList<F, S, T> extends AbstractClass implements Iterable<Triple<F, S, T>> {

    private final List<Triple<F, S, T>> list = new LinkedList<>();

    public void add(final F first, final S second, final T third) {
        list.add(Triple.create(first, second, third));
    }

    public Triple<F, S, T> getFirst() {
        return list.get(0);
    }

    public int size() {
        return list.size();
    }

    public List<Triple<F, S, T>> toList() {
        return new LinkedList<>(list);
    }

    @Override
    public Iterator<Triple<F, S, T>> iterator() {
        return list.iterator();
    }

}
