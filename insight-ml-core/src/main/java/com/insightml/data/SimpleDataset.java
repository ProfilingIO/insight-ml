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

import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.SimpleSample;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public final class SimpleDataset<I extends Sample, O> extends AbstractDataset<I, O> {

	private final Iterable<I> training;
	private final FeaturesConfig<I, O> config;

	public SimpleDataset(final String name, final Iterable<I> training, final FeaturesConfig<I, O> config) {
		super(name);
		this.training = training;
		this.config = config;
	}

	public static <S extends SimpleSample, O> SimpleDataset<S, O> create(final Iterable<S> instances) {
		return new SimpleDataset<>("SimpleDataset", instances, new AnonymousFeaturesConfig<>(instances,
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
	public Iterable<I> loadAll() {
		return loadTraining(null);
	}
}
