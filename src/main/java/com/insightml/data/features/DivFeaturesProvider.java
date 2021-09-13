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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
import com.insightml.models.FeaturesImpl;
import com.insightml.utils.Arrays;
import com.insightml.utils.IArguments;
import com.insightml.utils.types.DoublePair;

public final class DivFeaturesProvider<I extends Sample> extends AbstractFeatureProvider<I> {
	private final IFeatureProvider<I> baseProvider;

	public DivFeaturesProvider(final IFeatureProvider<I> baseProvider, final float defaultValue) {
		super("Div", defaultValue);
		this.baseProvider = baseProvider;
	}

	@Override
	public Pair<String[], Map<String, Stats>> featureNames(final IArguments arguments) {
		final CharSequence[] baseNames = baseProvider.featureNames(arguments).getFirst();
		final List<String> names = new LinkedList<>();
		for (int i = 0; i < baseNames.length; ++i) {
			if (!baseNames[i].toString().contains("/")) {
				for (int j = 0; j < baseNames.length; ++j) {
					if (i != j && !baseNames[j].toString().contains("/")) {
						names.add(baseNames[i] + "/" + baseNames[j]);
					}
				}
			}
		}
		return new Pair<>(Arrays.of(names, String.class), null);
	}

	@Override
	public void features(final I instance, final boolean isTraining, final IArguments arguments,
			final FeaturesConsumer features) {
		final FeaturesImpl baseFeatures = new FeaturesImpl();
		baseProvider.features(instance, isTraining, arguments, baseFeatures);
		for (final DoublePair<String> e1 : baseFeatures) {
			final double v1 = e1.getValue();
			for (final DoublePair<String> e2 : baseFeatures) {
				if (e1 != e2) {
					final double v2 = e2.getValue();
					features.add(e1.getKey() + "/" + e2.getKey(), v1 == 0 ? 0 : v2 == 0 ? 0 : v1 / v2);
				}
			}
		}
	}

}
