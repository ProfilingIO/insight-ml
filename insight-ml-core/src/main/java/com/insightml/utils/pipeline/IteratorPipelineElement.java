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
package com.insightml.utils.pipeline;

import java.util.Iterator;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IteratorPipelineElement<T> {
	private static final Logger LOG = LoggerFactory.getLogger(IteratorPipelineElement.class);

	private final Iterator<T> iterator;

	public IteratorPipelineElement(final Iterator<T> iterator) {
		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("Provided empty iterator");
		}
		this.iterator = iterator;
	}

	public <R> IteratorPipelineElement<R> map(final Function<T, R> function) {
		return new IteratorPipelineElement<>(new TransformedIterator<>(iterator, function));
	}

	public void consume(final PipelineConsumer<T> consumer) throws Exception {
		consume(consumer, -1);
	}

	public void consume(final PipelineConsumer<T> consumer, final int reportEveryNConsumptions) throws Exception {
		final long start = System.currentTimeMillis();
		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("Already consumed");
		}
		int consumed = 0;
		while (iterator.hasNext()) {
			consumer.consume(iterator.next());
			++consumed;
			if (reportEveryNConsumptions > 0 && consumed % reportEveryNConsumptions == 0) {
				LOG.info("Consumed {} pipeline elements", consumed);
			}
		}
		LOG.info("Consumed pipeline in {} ms", System.currentTimeMillis() - start);
	}

	private static final class TransformedIterator<T, R> implements Iterator<R> {
		private final Iterator<T> iterator;
		private final Function<T, R> function;

		public TransformedIterator(final Iterator<T> iterator, final Function<T, R> function) {
			this.iterator = iterator;
			this.function = function;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public R next() {
			return function.apply(iterator.next());
		}

	}
}
