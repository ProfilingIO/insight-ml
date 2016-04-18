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
package com.insightml;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.insightml.data.SimpleDataset;
import com.insightml.data.samples.AnonymousSample;

public class TestInstance extends AnonymousSample {

    private static final long serialVersionUID = -4381058641738370505L;

    private static int ids = 1;

    public TestInstance(final Object[] label, final double[] features, final String[] names) {
        super(++ids, label, features, names);
    }

    public static TestInstance creat(final Serializable label) {
        return new TestInstance(new Serializable[] {label }, new double[] {0.5 },
                new String[] {"dummy" });
    }

    public static Iterable<TestInstance> create(final String... labels) {
        final Set<String> labelz = new HashSet<>();
        labelz.add("dummyLabel");
        final List<TestInstance> instances = new LinkedList<>();
        for (final String label : labels) {
            instances.add(new TestInstance(new String[] {label }, new double[] {0.5 },
                    new String[] {"dummy" }));
            labelz.add(label);
        }
        return SimpleDataset.create(instances).loadAll();
    }

    @Override
    public final String toString() {
        return "TestInstance{" + getId() + "}";
    }

}
