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
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;

import com.insightml.utils.types.AbstractClass;

public abstract class ThreadedFor extends AbstractClass {

	public final void run(final int start, final int end) {
		final Queue<ForkJoinTask<?>> tasks = new LinkedList<>();
		for (int i = start; i < end; ++i) {
			final int j = i;
			tasks.add(JobPool.submit(() -> ThreadedFor.this.run(j)));
		}
		while (!tasks.isEmpty()) {
			try {
				tasks.poll().get();
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	protected abstract void run(final int i);

}
