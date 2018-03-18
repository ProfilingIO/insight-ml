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
package com.insightml.utils;

import java.util.Comparator;

public abstract class StrictComparator<T> implements Comparator<T> {

    @Override
    public final int compare(final T arg0, final T arg1) {
        final int comp = comp(arg0, arg1);
        if (comp == 0) {
            throw new IllegalStateException();
        }
        return comp;
    }

    protected abstract int comp(final T arg0, final T arg1);

}
