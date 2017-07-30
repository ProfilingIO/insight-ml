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
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Preconditions;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.Samples;
import com.insightml.data.samples.decorators.FeaturesDecorator;
import com.insightml.math.Normalization;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.types.AbstractConfigurable;

public final class PreprocessingPipeline<S extends Sample> extends AbstractConfigurable
		implements Serializable, IPreprocessingPipeline<S> {

	private static final long serialVersionUID = 3411531030881000331L;

	private IFeatureProvider<S> provider;
	private String[] featureNames;
	private Map<String, Stats> featureStats;
	private Normalization normalization;
	private IArguments arguments;

	PreprocessingPipeline() {
	}

	private PreprocessingPipeline(final IFeatureProvider<S> provider, final String[] featureNames,
			final Map<String, Stats> featureStats, final Normalization normalization, final IArguments arguments) {
		this.provider = Preconditions.checkNotNull(provider);
		this.featureNames = Preconditions.checkNotNull(featureNames);
		this.featureStats = featureStats;
		this.normalization = normalization;
		this.arguments = arguments;
	}

	@Nonnull
	public static <S extends Sample> PreprocessingPipeline<S> create(final Iterable<S> trainingSamples,
			final IFeatureProvider<S> provider, final IFeatureFilter filter, final Normalization normalization,
			final IArguments arguments) {
		final Pair<String[], Map<String, Stats>> featureSelection = provider
				.featureNames(new Samples<>(trainingSamples), arguments);
		return new PreprocessingPipeline<>(provider, filter.allowedFeatures(featureSelection.getFirst()),
				featureSelection.getSecond(), normalization, arguments);
	}

	@Override
	public <E> ISamples<S, E> run(final Iterable<S> input, final boolean isTraining) {
		final Samples<S, E> samples = new Samples<>(input, isTraining);
		return new FeaturesDecorator<>(samples, provider, featureNames, featureStats, normalization, isTraining,
				arguments);
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
	public Map<String, Stats> getFeatureStats() {
		return featureStats;
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
	public int hashCode() {
		return Arrays.hashCode(featureNames);
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
