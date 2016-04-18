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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

public abstract class ParallelFor<R> extends AbstractClass {

    public final Queue<R> run(final int start, final int end, final int directThreshold) {
        final List<Callable<R>> tasks = new LinkedList<>();
        Check.num(end, start + 1, 99999999);
        for (int i = start; i < end; ++i) {
            final int it = i;
            tasks.add(new Callable<R>() {
                @Override
                public R call() throws Exception {
                    try {
                        return exec(it);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            });
        }
        return JobPool.invokeAll(tasks, directThreshold);
    }

    protected abstract R exec(int i) throws Exception;

}
