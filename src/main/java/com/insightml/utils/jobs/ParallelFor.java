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
import java.util.concurrent.ExecutorService;
import java.util.function.IntFunction;

import com.insightml.utils.Check;

public final class ParallelFor {
	private ParallelFor() {
	}

	public static <R> Queue<R> run(final IntFunction<R> function, final int start, final int end,
			final int directThreshold) {
		final List<Callable<R>> tasks = new LinkedList<>();
		Check.num(end, start + 1, 99999999);
		for (int i = start; i < end; ++i) {
			tasks.add(new Execution<>(i, function));
		}
		return JobPool.invokeAll(tasks, directThreshold);
	}

	public static <R> Queue<R> run(final IntFunction<R> function, final int start, final int end,
			final int directThreshold, final ExecutorService executor) {
		final List<Callable<R>> tasks = new LinkedList<>();
		Check.num(end, start + 1, 99999999);
		for (int i = start; i < end; ++i) {
			tasks.add(new Execution<>(i, function));
		}
		return JobPool.execute(tasks, directThreshold, executor);
	}

	private static final class Execution<R> implements Callable<R> {
		private final int iteration;
		private final IntFunction<R> function;

		public Execution(final int iteration, final IntFunction<R> function) {
			this.iteration = iteration;
			this.function = function;
		}

		@Override
		public R call() {
			return function.apply(iteration);
		}
	}
}
