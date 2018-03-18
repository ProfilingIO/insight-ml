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
package com.insightml.data.features.stats;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.ISamples;
import com.insightml.math.Matrices;
import com.insightml.math.distributions.GaussianDistribution;
import com.insightml.utils.ui.IChartGui;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public final class FeaturesDistribution implements IUiProvider<ISamples<?, ?>> {

	public static void displayGui(final IChartGui gui, final ISamples<?, Double> training,
			final ISamples<?, Double> test, final int labelIndex) {
		final FeatureStatistics statsTrain = new FeatureStatistics(training, labelIndex);
		final FeatureStatistics statsTest = new FeatureStatistics(test, labelIndex);
		for (final String feature : training.featureNames()) {
			gui.addHistogram(null, null, Pair.create(feature, statsTrain), Pair.create(feature, statsTest));
		}
		gui.run();
	}

	@Override
	public String getText(final ISamples<?, ?> instances, final int labelIndex) {
		final StringBuilder builder = new StringBuilder();
		final double[][] table = instances.features();
		for (int f = 0; f < table[0].length; ++f) {
			final CharSequence feature = instances.featureNames()[f];
			builder.append(UiUtils.fill(feature, 40));
			builder.append(new GaussianDistribution(Matrices.column(table, f)) + "\n");
		}
		return builder.toString();
	}
}
