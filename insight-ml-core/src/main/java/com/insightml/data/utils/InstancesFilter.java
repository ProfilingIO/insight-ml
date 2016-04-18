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
package com.insightml.data.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Samples;
import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.utils.time.ITimeSystem;
import com.insightml.utils.time.TimeProvider;

public final class InstancesFilter {

	private InstancesFilter() {
	}

	public static <I extends ISample> Iterable<I> hasLabelSet(final Iterable<I> instances, final int labelIndex) {
		final List<I> filtered = new LinkedList<>();
		for (final I sample : instances) {
			if (sample.getExpected(labelIndex) != null) {
				filtered.add(sample);
			}
		}
		return filtered;
	}

	public static <I extends ISample> ISamples<I, ?> onLabel(final Iterable<I> instances, final int labelIndex,
			final int minLabel, final int maxLabel) {
		final List<I> filtered = new LinkedList<>();
		for (final I instance : instances) {
			final Number label = (Number) instance.getExpected(labelIndex);
			if (label != null && label.doubleValue() >= minLabel && label.doubleValue() <= maxLabel) {
				filtered.add(instance);
			}
		}
		return new Samples<>(filtered);
	}

	static <I extends ISample> ISamples<I, ?> filterBySmallestLabelSize(final Iterable<I> instances) {
		final List<I> filtered = new LinkedList<>();
		final List<I> instancesCopy = Lists.newLinkedList(instances);
		final int oldSize = instancesCopy.size();
		final int max = getMaxInstances(instances);

		final Map<String, Integer> labels = new HashMap<>();
		for (int i = 0; i < oldSize; ++i) {
			final int index = (int) (Math.random() * instancesCopy.size());
			final I instance = instancesCopy.remove(index);
			final String label = (String) instance.getExpected(0);
			final Integer count = labels.get(label);
			if (count == null || count.doubleValue() < max) {
				filtered.add(instance);
				labels.put(label, count == null ? 1 : count + 1);
			}
		}
		return new Samples<>(filtered);
	}

	private static <I extends ISample> int getMaxInstances(final Iterable<I> instances) {
		final IntSumMapBuilder<String> labels = IntSumMap.builder(false, 16);
		for (final I instance : instances) {
			labels.increment((String) instance.getExpected()[0], 1);
		}
		return (int) labels.build(0).statistics().getMin();
	}

	public static <T extends ITimeSystem<T>, I extends TimeProvider<T>> Collection<I> filterByMarginDate(
			final Collection<I> instances, final T marginDate) {
		return Collections2.filter(instances, input -> input.getDate().before(marginDate));
	}
}
