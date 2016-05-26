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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;

import com.insightml.utils.Check;

public final class JobPool {
	private static ForkJoinPool pool;

	private JobPool() {
	}

	public static synchronized void setParallelism(final int parallelism) {
		pool = new ForkJoinPool(parallelism);
	}

	private static synchronized ForkJoinPool getPool() {
		if (pool == null) {
			pool = new ForkJoinPool();
		}
		return pool;
	}

	public static ForkJoinTask<?> submit(final Runnable runnable) {
		return getPool().submit(runnable);
	}

	public static <T> Queue<T> invokeAll(final List<Callable<T>> tasks, final int directThreshold) {
		Check.state(directThreshold >= 1);
		if (tasks.size() <= directThreshold) {
			return runDirectly(tasks);
		}
		final ForkJoinPool pol = getPool();
		if (pol.getParallelism() <= 1) {
			return runDirectly(tasks);
		}
		final Queue<T> results = new LinkedList<>();
		for (final Future<T> result : pol.invokeAll(tasks)) {
			try {
				results.add(result.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
		return results;
	}

	private static <T> Queue<T> runDirectly(final List<Callable<T>> tasks) {
		final Queue<T> list = new LinkedList<>();
		for (final Callable<T> item : tasks) {
			try {
				list.add(item.call());
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return list;
	}

}
