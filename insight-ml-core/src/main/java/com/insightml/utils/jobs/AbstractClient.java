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

import com.insightml.utils.types.AbstractClass;

public abstract class AbstractClient extends AbstractClass implements IClient {

    @Override
    public final <O> IJobBatch<O> newBatch() {
        return new Batch<>();
    }

    protected abstract <O> Collection<O> execute(Iterable<IJob<O>> jobs);

    final class Batch<O> implements IJobBatch<O> {

        private List<IJob<O>> jobList = new LinkedList<>();

        @Override
        public IJobBatch<O> addJob(final IJob<O> job) {
            jobList.add(job);
            return this;
        }

        @Override
        public List<O> run() {
            final Collection<O> output = execute(jobList);
            jobList = null;
            return (List<O>) output;
        }

        @Override
        public int size() {
            return jobList.size();
        }

    }

}
