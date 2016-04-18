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
package com.insightml.utils.jobs;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.insightml.utils.Pair;

public final class ThreadedClient extends AbstractClient {

    @Override
    protected <O> Collection<O> execute(final Iterable<IJob<O>> jobs) {
        final List<O> values = new LinkedList<>();
        for (final Pair<IJob<O>, O> val : new Threaded<IJob<O>, O>() {
            @Override
            protected O exec(final int i, final IJob<O> input) throws Exception {
                return input.run();
            }
        }.run(jobs, 1)) {
            values.add(val.getSecond());
        }
        return values;
    }

}
