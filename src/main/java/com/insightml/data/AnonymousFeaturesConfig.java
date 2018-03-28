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
package com.insightml.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Preconditions;
import com.insightml.data.features.AggregateFeatureProvider;
import com.insightml.data.features.DivFeaturesProvider;
import com.insightml.data.features.FeaturesConsumer;
import com.insightml.data.features.GeneralFeatureProvider;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.SimpleFeaturesProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.Sample;
import com.insightml.utils.IArguments;

public final class AnonymousFeaturesConfig<S extends Sample, O> extends FeaturesConfig<S, O> {
	private static final long serialVersionUID = -8466353461597201244L;

	private final IFeatureProvider<S> provider;
	private IFeatureFilter filter;

	public AnonymousFeaturesConfig(final IFeatureProvider<S> provider, final IFeatureFilter filter) {
		super(null, null);
		this.provider = provider;
		this.filter = filter;
	}

	public AnonymousFeaturesConfig(final String[] features, final SimpleFeaturesProvider<S> simpleFeaturesProvider,
			final double defaultValue, final boolean useDivFeaturesProvider) {
		super(null, null);
		this.provider = provider(features, simpleFeaturesProvider, defaultValue, useDivFeaturesProvider);
	}

	public AnonymousFeaturesConfig(final Iterable<S> examples, final SimpleFeaturesProvider<S> exampleFeaturesProvider,
			final double defaultValue, final boolean useDivFeaturesProvider, final IFeatureFilter filter) {
		this(examples, exampleFeaturesProvider, 1, defaultValue, useDivFeaturesProvider, filter);
	}

	public AnonymousFeaturesConfig(final Iterable<S> examples, final SimpleFeaturesProvider<S> exampleFeaturesProvider,
			final int minOccurrences, final double defaultValue, final boolean useDivFeaturesProvider,
			final IFeatureFilter filter) {
		this(examples.iterator(), exampleFeaturesProvider, minOccurrences, defaultValue, useDivFeaturesProvider,
				filter);
	}

	public AnonymousFeaturesConfig(final Iterator<S> examples, final SimpleFeaturesProvider<S> exampleFeaturesProvider,
			final int minOccurrences, final double defaultValue, final boolean useDivFeaturesProvider,
			final IFeatureFilter filter) {
		super(null, null);
		this.provider = fromExamples(examples,
				exampleFeaturesProvider,
				minOccurrences,
				defaultValue,
				useDivFeaturesProvider);
		this.filter = Preconditions.checkNotNull(filter);
	}

	public static <S extends Sample, O> AnonymousFeaturesConfig<S, O> of(final Iterable<S> examples,
			final SimpleFeaturesProvider<S> exampleFeaturesProvider, final double defaultValue) {
		return of(examples, exampleFeaturesProvider, 1, defaultValue);
	}

	public static <S extends Sample, O> AnonymousFeaturesConfig<S, O> of(final Iterable<S> examples,
			final SimpleFeaturesProvider<S> exampleFeaturesProvider, final int minOccurrences,
			final double defaultValue) {
		return new AnonymousFeaturesConfig<>(examples, exampleFeaturesProvider, minOccurrences, defaultValue, false,
				new IgnoreFeatureFilter());
	}

	@Override
	public IFeatureProvider<S> newFeatureProvider() {
		return provider;
	}

	@Override
	public IFeatureFilter newFeatureFilter() {
		return filter;
	}

	private static <S extends Sample> IFeatureProvider<S> fromExamples(final Iterator<S> examples,
			final SimpleFeaturesProvider<S> simpleFeaturesProvider, final int minOccurrences, final double defaultValue,
			final boolean useDivFeaturesProvider) {
		final Map<String, Integer> names = new LinkedHashMap<>();
		while (examples.hasNext()) {
			simpleFeaturesProvider.apply(examples.next(), (k, v) -> names.merge(k, 1, Integer::sum));
		}
		return createFromFrequencies(simpleFeaturesProvider,
				minOccurrences,
				defaultValue,
				useDivFeaturesProvider,
				names);
	}

	public static <S extends Sample> IFeatureProvider<S> createFromFrequencies(
			final SimpleFeaturesProvider<S> simpleFeaturesProvider, final int minOccurrences, final double defaultValue,
			final boolean useDivFeaturesProvider, final Map<String, Integer> names) {
		final Set<String> selected = new LinkedHashSet<>();
		for (final Entry<String, Integer> entry : names.entrySet()) {
			if (entry.getValue() >= minOccurrences) {
				selected.add(entry.getKey());
			}
		}
		return provider(selected.toArray(new String[selected.size()]),
				simpleFeaturesProvider,
				defaultValue,
				useDivFeaturesProvider);
	}

	private static <S extends Sample> IFeatureProvider<S> provider(final String[] featureNames,
			final SimpleFeaturesProvider<S> simpleFeaturesProvider, final double defaultValue,
			final boolean useDivFeaturesProvider) {
		final SimpleFeatureProvider<S> prov = new SimpleFeatureProvider<>(featureNames, simpleFeaturesProvider,
				defaultValue);
		if (!useDivFeaturesProvider) {
			return prov;
		}
		return new AggregateFeatureProvider<>("features", defaultValue,
				Arrays.asList(prov, new DivFeaturesProvider<>(prov, defaultValue)));
	}

	@Override
	public int hashCode() {
		return Objects.hash(provider, filter);
	}

	public static final class SimpleFeatureProvider<S extends Sample> extends GeneralFeatureProvider<S> {
		private final List<Pair<String, String>> features;
		private final SimpleFeaturesProvider<S> simpleFeaturesProvider;

		public SimpleFeatureProvider(final String[] featureNames,
				final SimpleFeaturesProvider<S> simpleFeaturesProvider, final double defaultValue) {
			super("features", defaultValue);
			this.features = new LinkedList<>();
			for (final CharSequence feat : featureNames) {
				features.add(new Pair<>(feat.toString(), ""));
			}
			this.simpleFeaturesProvider = simpleFeaturesProvider;
		}

		@Override
		protected List<Pair<String, String>> getFeatures() {
			return features;
		}

		@Override
		public void features(final S sample, final boolean isTraining, final IArguments arguments,
				final FeaturesConsumer consumer) {
			simpleFeaturesProvider.apply(sample, consumer);
		}

		@Override
		public int hashCode() {
			return Objects.hash(features);
		}
	}

}
