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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Preconditions;
import com.insightml.data.features.AggregateFeatureProvider;
import com.insightml.data.features.DivFeaturesProvider;
import com.insightml.data.features.GeneralFeatureProvider;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.Sample;
import com.insightml.models.Features;
import com.insightml.utils.types.DoublePair;

public final class AnonymousFeaturesConfig<S extends Sample, O> extends FeaturesConfig<S, O> {

	private static final long serialVersionUID = -8466353461597201244L;

	private final IFeatureProvider<S> provider;
	private final boolean useDivFeaturesProvider;
	private IFeatureFilter filter;

	public AnonymousFeaturesConfig(final String[] features, final Function<S, Features> simpleFeaturesProvider,
			final double defaultValue, final boolean useDivFeaturesProvider) {
		super(null, null);
		this.provider = provider(features, simpleFeaturesProvider, defaultValue);
		this.useDivFeaturesProvider = useDivFeaturesProvider;
	}

	public AnonymousFeaturesConfig(final Iterable<S> examples, final Function<S, Features> exampleFeaturesProvider,
			final double defaultValue, final boolean useDivFeaturesProvider, final IFeatureFilter filter) {
		super(null, null);
		this.provider = fromExamples(examples, exampleFeaturesProvider, defaultValue);
		this.useDivFeaturesProvider = useDivFeaturesProvider;
		this.filter = Preconditions.checkNotNull(filter);
	}

	public static <S extends Sample, O> AnonymousFeaturesConfig<S, O> of(final Iterable<S> examples,
			final Function<S, Features> exampleFeaturesProvider, final double defaultValue) {
		return new AnonymousFeaturesConfig<>(examples, exampleFeaturesProvider, defaultValue, false,
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

	private IFeatureProvider<S> fromExamples(final Iterable<S> examples,
			final Function<S, Features> simpleFeaturesProvider, final double defaultValue) {
		final Set<String> names = new LinkedHashSet<>();
		for (final S example : examples) {
			for (final DoublePair<String> feat : simpleFeaturesProvider.apply(example)) {
				names.add(feat.getKey());
			}
		}
		return provider(names.toArray(new String[names.size()]), simpleFeaturesProvider, defaultValue);
	}

	private IFeatureProvider<S> provider(final String[] featureNames,
			final Function<S, Features> simpleFeaturesProvider, final double defaultValue) {
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
		return Objects.hash(provider, useDivFeaturesProvider, filter);
	}

	public static final class SimpleFeatureProvider<S extends Sample> extends GeneralFeatureProvider<S> {
		private final List<Pair<String, String>> features;
		private final Function<S, Features> simpleFeaturesProvider;

		public SimpleFeatureProvider(final String[] featureNames, final Function<S, Features> simpleFeaturesProvider,
				final double defaultValue) {
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
		public Features features(final S sample, final boolean isTraining) {
			return simpleFeaturesProvider.apply(sample);
		}

		@Override
		public int hashCode() {
			return Objects.hash(features);
		}
	}

}
