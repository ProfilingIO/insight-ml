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
package com.insightml.data.features;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public class AggregateFeatureProvider<I extends Sample> extends AbstractFeatureProvider<I> {
	private final List<IFeatureProvider<I>> providers;

	public AggregateFeatureProvider(final String name, final double defaultValue,
			final List<IFeatureProvider<I>> providers) {
		super(name, defaultValue);
		this.providers = Check.size(providers, 2, 99);
	}

	@Override
	public final Pair<String[], Map<String, Stats>> featureNames(final IArguments arguments) {
		final List<String> names = new LinkedList<>();
		for (final IFeatureProvider<I> provider : providers) {
			for (final String namee : provider.featureNames(arguments).getFirst()) {
				names.add(namee);
			}
		}
		return new Pair<>(Arrays.of(names, String.class), null);
	}

	@Override
	public final void features(final I instance, final boolean isTraining, final IArguments arguments,
			final FeaturesConsumer features) {
		for (final IFeatureProvider<I> provider : providers) {
			provider.features(instance, isTraining, arguments, features);
		}
	}

	@Override
	public final String getReport() {
		final StringBuilder builder = new StringBuilder();
		for (final IFeatureProvider<I> provider : providers) {
			builder.append("\n" + provider.getReport());
		}
		return builder.toString();
	}

}
