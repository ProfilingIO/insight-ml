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
package com.insightml.data.features;

import java.util.List;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Preconditions;
import com.insightml.data.samples.ISample;

public abstract class GeneralFeatureProvider<I extends ISample> extends AbstractFeatureProvider<I> {

	protected GeneralFeatureProvider() {
	}

	public GeneralFeatureProvider(final String name, final double defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public final String[] featureNames(final Iterable<I> samples) {
		final List<Pair<String, String>> features = getFeatures();
		final String[] names = new String[features.size()];
		int i = -1;
		for (final Pair<String, String> feature : features) {
			names[++i] = Preconditions.checkNotNull(feature.getFirst());
		}
		return names;
	}

	protected abstract List<Pair<String, String>> getFeatures();

	@Override
	public String getReport() {
		return getName() + "\n" + getFeatures();
	}
}
