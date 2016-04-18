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

import com.insightml.data.samples.ISample;
import com.insightml.models.Features;
import com.insightml.models.FeaturesImpl;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.types.DoublePair;

public class AggregateFeatureProvider<I extends ISample> extends AbstractFeatureProvider<I> {
	private final List<IFeatureProvider<I>> providers;

	public AggregateFeatureProvider(final String name, final double defaultValue,
			final List<IFeatureProvider<I>> providers) {
		super(name, defaultValue);
		this.providers = Check.size(providers, 1, 99);
	}

	@Override
	public final String[] featureNames(final Iterable<I> samples) {
		final List<String> names = new LinkedList<>();
		for (final IFeatureProvider<I> provider : providers) {
			for (final String namee : provider.featureNames(samples)) {
				names.add(namee);
			}
		}
		return Arrays.of(names, String.class);
	}

	@Override
	public final Features features(final I instance, final boolean isTraining) {
		final FeaturesImpl features = new FeaturesImpl();
		for (final IFeatureProvider<I> provider : providers) {
			for (final DoublePair<String> feat : provider.features(instance, isTraining)) {
				features.add(feat.getKey(), feat.getValue());
			}
		}
		return features;
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
