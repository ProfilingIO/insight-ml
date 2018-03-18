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
package com.insightml.models;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.insightml.models.LearnerArguments.Argument;
import com.insightml.utils.Check;

public final class LearnerArguments implements Iterable<Argument> {

    private final Map<String, Argument> map = new LinkedHashMap<>();

    public void add(final String arg, final Double def, final double min, final double max) {
        map.put(arg, new Argument(arg, def, min, max));
    }

    public Argument get(final String arg) {
        return Check.notNull(map.get(arg), "The argument " + arg + " is not registered.");
    }

    public int size() {
        return map.size();
    }

    @Override
    public Iterator<Argument> iterator() {
        return map.values().iterator();
    }

    public static final class Argument {

        private final String arg;
        private final Double def;
        private final double min;
        private final double max;

        public Argument(final String arg, final Double def, final double min, final double max) {
            this.arg = arg;
            this.def = def;
            this.min = min;
            this.max = Check.num(max, min, 999999999);
        }

        public String getName() {
            return arg;
        }

        public Double getDefault() {
            return def;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double validate(final double val) {
            return Check.num(val, min, max, arg);
        }
    }
}
