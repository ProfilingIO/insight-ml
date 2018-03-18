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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;

import com.insightml.utils.types.AbstractClass;

public abstract class SimpleThreaded<I> extends AbstractClass {

	// TODO: Runnable version
	public final int run(final Iterable<? extends I> queue) {
		final Queue<ForkJoinTask<?>> tasks = new LinkedList<>();
		int i = -1;
		for (final I it : queue) {
			final int j = ++i;
			tasks.add(JobPool.submit(() -> {
				try {
					SimpleThreaded.this.run(j, it);
				} catch (final IOException e) {
					throw new IllegalStateException(e);
				}
			}));
		}
		while (!tasks.isEmpty()) {
			try {
				tasks.poll().get();
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
		return i + 1;
	}

	public final int run(final Collection<? extends I> queue, final int directThreshold) {
		if (queue.size() < directThreshold) {
			int i = -1;
			for (final I task : queue) {
				++i;
				try {
					run(i, task);
				} catch (final IOException e) {
					throw new IllegalStateException(e);
				}
			}
			return iterative(queue);
		}
		return run(queue);
	}

	public final int run(final Iterable<? extends I> queue, final boolean forceNonparallel) {
		if (forceNonparallel) {
			return iterative(queue);
		}
		return run(queue);
	}

	private int iterative(final Iterable<? extends I> queue) {
		int i = -1;
		for (final I task : queue) {
			++i;
			try {
				run(i, task);
			} catch (final IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return i + 1;
	}

	protected abstract void run(final int i, I input) throws IOException;

}
