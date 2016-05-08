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
import java.util.concurrent.Callable;

import org.apache.commons.math3.util.Pair;

import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.collections.ArrayIterator;

public abstract class Threaded<I, O> extends AbstractClass {

	public final List<Pair<I, O>> run(final I[] input, final int directThreshold) {
		return run(new ArrayIterator<>(input), directThreshold);
	}

	public final List<Pair<I, O>> run(final Iterable<? extends I> queue, final int directThreshold) {
		final List<Callable<Pair<I, O>>> tasks = new LinkedList<>();
		int i = -1;
		for (final I it : queue) {
			final int j = ++i;
			tasks.add(new Callable<Pair<I, O>>() {
				@Override
				public Pair<I, O> call() {
					try {
						return new Pair<>(it, exec(j, it));
					} catch (final Exception e) {
						e.printStackTrace();
						throw new IllegalStateException(e);
					}
				}
			});
		}
		return new LinkedList<>(JobPool.invokeAll(tasks, directThreshold));
	}

	protected abstract O exec(int i, I input) throws Exception;

}
