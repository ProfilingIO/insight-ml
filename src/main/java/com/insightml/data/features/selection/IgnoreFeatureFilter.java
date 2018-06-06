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
package com.insightml.data.features.selection;

import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.samples.Sample;
import com.insightml.utils.types.AbstractClass;

public final class IgnoreFeatureFilter extends AbstractClass implements IFeatureFilter, FeatureFilterFactory {
	private static final long serialVersionUID = -7692774720196688190L;

	private static IgnoreFeatureFilter INSTANCE = new IgnoreFeatureFilter();

	@Override
	public <I extends Sample> IFeatureFilter createFilter(final Iterable<I> instances,
			final IFeatureProvider<I> provider, final int labelIndex) {
		return INSTANCE;
	}

	@Override
	public void ignoreFeature(final String feature) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] allowedFeatures(final String[] features) {
		return features;
	}

	@Override
	public int hashCode() {
		return "IgnoreFeatureFilter".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IgnoreFeatureFilter;
	}

	@Override
	public String toString() {
		return "IgnoreFeatureFilter";
	}

}
