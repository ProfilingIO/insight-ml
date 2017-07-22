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

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.Samples;
import com.insightml.data.samples.decorators.FeaturesDecorator;
import com.insightml.math.Normalization;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractConfigurable;

public final class PreprocessingPipeline<S extends Sample> extends AbstractConfigurable
		implements Serializable, IPreprocessingPipeline<S> {

	private static final long serialVersionUID = 3411531030881000331L;

	private IFeatureProvider<S> provider;
	private String[] featureNames;
	private Normalization normalization;

	PreprocessingPipeline() {
	}

	private PreprocessingPipeline(final IFeatureProvider<S> provider, final String[] featureNames,
			final Normalization normalization) {
		this.provider = Preconditions.checkNotNull(provider);
		this.featureNames = Preconditions.checkNotNull(featureNames);
		this.normalization = normalization;
	}

	public static <S extends Sample> PreprocessingPipeline<S> create(final Iterable<S> trainingSamples,
			final IFeatureProvider<S> provider, final IFeatureFilter filter, final Normalization normalization) {
		return new PreprocessingPipeline<>(provider,
				filter.allowedFeatures(provider.featureNames(new Samples<>(trainingSamples))), normalization);
	}

	public static <S extends Sample> PreprocessingPipeline<S> create(final Iterable<S> trainingSamples,
			final FeaturesConfig<S, ?> config) {
		return create(trainingSamples,
				config.newFeatureProvider(),
				config.newFeatureFilter(),
				config.getNormalization());
	}

	@Override
	public <E> ISamples<S, E> run(final Iterable<S> input, final boolean isTraining) {
		final Samples<S, E> samples = new Samples<>(input, isTraining);
		return new FeaturesDecorator<>(samples, provider, featureNames, normalization, isTraining);
	}

	@Override
	public IFeatureProvider<S> getProvider() {
		return provider;
	}

	@Override
	public String[] getFeatureNames() {
		return featureNames;
	}

	@Override
	public Normalization getNormalization() {
		return normalization;
	}

	@Override
	public String getReport() {
		return provider.getReport();
	}

	@Override
	public boolean equals(final Object obj) {
		final PreprocessingPipeline<?> oth = (PreprocessingPipeline<?>) obj;
		Check.state(provider.equals(oth.provider), provider);
		return Objects.equals(normalization, oth.normalization);
	}

	@Override
	public Object[] getComponents() {
		return new Object[] { provider, normalization };
	}

}
