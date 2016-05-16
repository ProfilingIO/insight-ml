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
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Samples;
import com.insightml.data.samples.decorators.FeaturesDecorator;
import com.insightml.math.Normalization;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractConfigurable;

public final class PreprocessingPipeline<S extends Sample, E> extends AbstractConfigurable
		implements Serializable, IPreprocessingPipeline<S, E> {

	private static final long serialVersionUID = 3411531030881000331L;

	private IFeatureProvider<S> provider;
	private IFeatureFilter filter;
	private Normalization normalization;

	PreprocessingPipeline() {
	}

	public PreprocessingPipeline(final IFeatureProvider<S> provider, final IFeatureFilter filter,
			final Normalization normalization) {
		this.provider = Preconditions.checkNotNull(provider);
		this.filter = Preconditions.checkNotNull(filter);
		this.normalization = normalization;
	}

	public static <S extends Sample, E> PreprocessingPipeline<S, E> create(final FeaturesConfig<S, ?> config,
			final Iterable<S> training, final Integer labelIndex, final Iterable<S>[] instances) {
		final IFeatureProvider<S> featureProvider = config.newFeatureProvider(training, instances);
		return new PreprocessingPipeline<>(featureProvider,
				config.newFeatureFilter(training, featureProvider, labelIndex), config.getNormalization());
	}

	@Override
	public ISamples<S, E> run(final Iterable<S> input, final boolean isTraining) {
		return new FeaturesDecorator<>(new Samples<S, E>(input), provider, filter, normalization, isTraining);
	}

	public IFeatureProvider<S> getProvider() {
		return provider;
	}

	@Override
	public String getReport() {
		return provider.getReport();
	}

	@Override
	public boolean equals(final Object obj) {
		final PreprocessingPipeline<?, ?> oth = (PreprocessingPipeline<?, ?>) obj;
		Check.state(provider.equals(oth.provider), provider);
		Check.state(filter.equals(oth.filter));
		return Objects.equals(normalization, oth.normalization);
	}

	@Override
	public Object[] getComponents() {
		return new Object[] { provider, filter, normalization };
	}

}
