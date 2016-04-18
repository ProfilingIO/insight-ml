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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Sets {

    private Sets() {
    }

    public static <T> HashSet<T> create(final int expected) {
        return new HashSet<>(capacity(expected));
    }

    public static <I> Set<I> newConcurrentSet(final int capacity) {
        return java.util.Collections.newSetFromMap(new ConcurrentHashMap<I, Boolean>(
                capacity(capacity)));
    }

    public static int capacity(final int expectedSize) {
        // Check.range(expectedSize, 3, 1141399);
        if (expectedSize < 1 << Integer.SIZE - 2) {
            return expectedSize + expectedSize / 3;
        }
        Check.state(false);
        return Integer.MAX_VALUE;
    }

}
