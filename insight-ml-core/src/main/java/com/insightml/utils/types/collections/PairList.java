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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.insightml.utils.Pair;
import com.insightml.utils.types.AbstractClass;

public final class PairList<F, S> extends AbstractClass implements Iterable<Pair<F, S>>,
Serializable {

    private static final long serialVersionUID = 7289587127664569567L;

    private final LinkedList<Pair<F, S>> list = new LinkedList<>();
    private final boolean allowDuplicateKeys;

    public PairList() {
        this.allowDuplicateKeys = false;
    }

    public PairList(final boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
    }

    public PairList(final Collection<Pair<F, S>> values) {
        this();
        list.addAll(values);
    }

    public synchronized void add(final F first, final S second) {
        if (!allowDuplicateKeys) {
            for (final Pair<F, S> entry : list) {
                if (first.equals(entry.getFirst())) {
                    throw new IllegalStateException("(" + first + "," + second + ") equals "
                            + entry);
                }
            }
        }
        list.add(Pair.create(first, second));
    }

    public void addAll(final PairList<F, S> lst) {
        for (final Pair<F, S> entry : lst) {
            add(entry.getFirst(), entry.getSecond());
        }
    }

    public Pair<F, S> get(final int index) {
        return list.get(index);
    }

    public Pair<F, S> getFirst() {
        return list.isEmpty() ? null : list.getFirst();
    }

    public Pair<F, S> getLast() {
        return list.getLast();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    @Override
    public Iterator<Pair<F, S>> iterator() {
        return list.iterator();
    }

    public PairList<F, S> subList(final int limit) {
        return list.size() < limit ? this : new PairList<>(list.subList(0, limit));
    }

    public LinkedList<Pair<F, S>> asList() {
        return new LinkedList<>(list);
    }

    public Map<F, S> asMap() {
        final Map<F, S> map = new LinkedHashMap<>();
        for (final Pair<F, S> entry : list) {
            // TODO: Check for duplicate keys!
            map.put(entry.getFirst(), entry.getSecond());
        }
        return map;
    }

    public Queue<F> keys() {
        final Queue<F> values = new LinkedList<>();
        for (final Pair<F, S> entry : list) {
            values.add(entry.getFirst());
        }
        return values;
    }

    public LinkedList<S> values() {
        final LinkedList<S> values = new LinkedList<>();
        for (final Pair<F, S> entry : list) {
            if (entry.getSecond() != null) {
                values.add(entry.getSecond());
            }
        }
        return values;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean equals(final Object obj) {
        final PairList<?, ?> oth = (PairList<?, ?>) obj;
        return allowDuplicateKeys == oth.allowDuplicateKeys && list.equals(oth.list);
    }

    @Override
    public String toString() {
        return list.toString() + ";" + allowDuplicateKeys;
    }

}
