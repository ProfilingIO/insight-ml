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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.insightml.data.features.AggregateFeatureProvider;
import com.insightml.data.features.DivFeaturesProvider;
import com.insightml.data.features.GeneralFeatureProvider;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.samples.AnonymousSample;
import com.insightml.models.Features;
import com.insightml.utils.Check;
import com.insightml.utils.Pair;
import com.insightml.utils.types.DoublePair;
import com.insightml.utils.types.collections.ArrayIterator;

public final class AnonymousFeaturesConfig<S extends AnonymousSample, O> extends FeaturesConfig<S, O> {

	private static final long serialVersionUID = -8466353461597201244L;

	private final IFeatureProvider<S> provider;
	private final boolean useDivFeaturesProvider;
	private IFeatureFilter filter;

	public AnonymousFeaturesConfig(final String[] features, final double defaultValue,
			final boolean useDivFeaturesProvider) {
		super(null, null);
		this.provider = provider(new ArrayIterator<>(features), defaultValue);
		this.useDivFeaturesProvider = useDivFeaturesProvider;
	}

	public AnonymousFeaturesConfig(final Iterable<S> examples, final double defaultValue,
			final boolean useDivFeaturesProvider, final IFeatureFilter filter) {
		super(null, null);
		this.provider = fromExamples(examples, defaultValue);
		this.useDivFeaturesProvider = useDivFeaturesProvider;
		this.filter = Check.notNull(filter);
	}

	@Override
	public IFeatureProvider<S> newFeatureProvider(final Iterable<S> training, final Iterable<S>[] rest) {
		return provider;
	}

	@Override
	public IFeatureFilter newFeatureFilter(final Iterable<S> training, final IFeatureProvider<S> prov,
			final Integer labelIndex) {
		return filter;
	}

	private IFeatureProvider<S> fromExamples(final Iterable<S> examples, final double defaultValue) {
		final Set<String> names = new LinkedHashSet<>();
		for (final S example : examples) {
			for (final DoublePair<String> feat : example.loadFeatures()) {
				names.add(feat.getKey());
			}
		}
		return provider(names, defaultValue);
	}

	private IFeatureProvider<S> provider(final Iterable<String> names, final double defaultValue) {
		final List<Pair<String, String>> features = new LinkedList<>();
		for (final CharSequence feat : names) {
			features.add(new Pair<>(feat.toString(), ""));
		}
		final IFeatureProvider<S> prov = new GeneralFeatureProvider<S>("features", defaultValue) {
			@Override
			protected List<Pair<String, String>> getFeatures() {
				return features;
			}

			@Override
			public Features features(final S instance, final boolean isTraining) {
				return ((AnonymousSample) instance).loadFeatures();
			}
		};
		final List<IFeatureProvider<S>> providers = new ArrayList<>(2);
		providers.add(prov);
		if (useDivFeaturesProvider) {
			providers.add(new DivFeaturesProvider<>(prov, defaultValue));
		}
		return new AggregateFeatureProvider<>("features", defaultValue, providers);
	}

}
