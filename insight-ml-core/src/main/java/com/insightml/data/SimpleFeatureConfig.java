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

import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.Sample;

public final class SimpleFeatureConfig<I extends Sample, P> extends FeaturesConfig<I, P> {
	private static final long serialVersionUID = -9027051549538426023L;

	private final IFeatureProvider<I> provider;
	private IFeatureFilter filter;

	public SimpleFeatureConfig(final IFeatureProvider<I> provider, final Function<P, P> postProcessor) {
		this(provider, new IgnoreFeatureFilter(), postProcessor);
	}

	public SimpleFeatureConfig(final IFeatureProvider<I> provider, final IFeatureFilter filter,
			final Function<P, P> postProcessor) {
		super(null, postProcessor);
		this.provider = provider;
		this.filter = Preconditions.checkNotNull(filter);
	}

	@Override
	public IFeatureProvider<I> newFeatureProvider() {
		return provider;
	}

	@Override
	public IFeatureFilter newFeatureFilter() {
		return filter;
	}
}
