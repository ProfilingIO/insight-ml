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

import com.insightml.data.features.SimpleFeaturesProvider;
import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.SimpleSample;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public final class SimpleDataset<I extends Sample, O> extends AbstractDataset<I, O> {

	private final @Nonnull Collection<I> training;
	private final FeaturesConfig<I, O> config;

	public SimpleDataset(final String name, final @Nonnull Collection<I> training, final FeaturesConfig<I, O> config) {
		super(name);
		this.training = training;
		this.config = config;
	}

	public static <S extends Sample, O> SimpleDataset<S, O> create(final @Nonnull Collection<S> samples,
			final SimpleFeaturesProvider<S> featuresProvider) {
		return new SimpleDataset<>("SimpleDataset", samples,
				AnonymousFeaturesConfig.of(samples.stream(), featuresProvider, 0));
	}

	public static <S extends SimpleSample, O> SimpleDataset<S, O> create(final @Nonnull Collection<S> instances) {
		return new SimpleDataset<>("SimpleDataset", instances, new AnonymousFeaturesConfig<>(instances.stream(),
				SimpleSample::loadFeatures, -9999999.0, false, new IgnoreFeatureFilter()));
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
}
