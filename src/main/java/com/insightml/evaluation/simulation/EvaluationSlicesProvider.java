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
package com.insightml.evaluation.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.insightml.data.samples.Sample;

public interface EvaluationSlicesProvider<I extends Sample> {

	@Nonnull
	default Set<String>[] getSlices(@Nonnull final Iterable<I> samples) {
		final List<Set<String>> slices = new ArrayList<>();
		for (final I sample : samples) {
			slices.add(getSlices(sample));
		}
		return slices.toArray(Set[]::new);
	}

	@Nonnull
	Set<String> getSlices(@Nonnull I sample);
}
