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

import java.util.Collection;

import javax.annotation.Nonnull;

import com.insightml.data.features.FeaturesConsumer;
import com.insightml.data.features.SimpleFeaturesProvider;
import com.insightml.data.features.selection.FeatureFilterFactory;
import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.SimpleSample;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public final class SimpleDataset<I extends Sample, O> extends AbstractDataset<I, O> {

	@Nonnull
	private final Collection<I> training;
	private final FeaturesConfig<I, O> config;

	public SimpleDataset(final String name, @Nonnull final Collection<I> training, final FeaturesConfig<I, O> config) {
		super(name);
		this.training = training;
		this.config = config;
	}

	public static <S extends Sample, O> SimpleDataset<S, O> create(@Nonnull final Collection<S> samples,
			final SimpleFeaturesProvider<S> featuresProvider) {
		return create(samples, featuresProvider, new IgnoreFeatureFilter());
	}

	public static <S extends Sample, O> SimpleDataset<S, O> create(@Nonnull final Collection<S> samples,
			final SimpleFeaturesProvider<S> featuresProvider, final FeatureFilterFactory featureFilter) {
		return new SimpleDataset<>("SimpleDataset",
				samples,
				new AnonymousFeaturesConfig<>(samples.stream(), featuresProvider, 1, 0, false, featureFilter));
	}

	public static <S extends SimpleSample, O> SimpleDataset<S, O> create(@Nonnull final Collection<S> instances) {
		return new SimpleDataset<>("SimpleDataset",
				instances,
				new AnonymousFeaturesConfig<>(instances.stream(),
						new SimpleSamplesFeatureProvider<>(),
						-9999999.0f,
						false,
						new IgnoreFeatureFilter()));
	}

	@Override
	public FeaturesConfig<I, O> getFeaturesConfig(final IArguments arguments) {
		return config;
	}

	@Override
	public Iterable<I> loadTraining(final Integer labelIndex) {
		Check.argument(labelIndex == null || labelIndex == 0);
		return training;
	}

	@Override
	public Collection<I> loadAll() {
		return training;
	}

	public static final class SimpleSamplesFeatureProvider<S extends SimpleSample>
			implements SimpleFeaturesProvider<S> {

		@Override
		public String getName() {
			return "SimpleSamplesFeatureProvider";
		}

		@Override
		public void apply(final S sample, final FeaturesConsumer consumer) {
			sample.loadFeatures(consumer);
		}

	}
}
