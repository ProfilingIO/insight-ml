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

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.Lists;
import com.insightml.data.samples.ISamples;
import com.insightml.math.statistics.Correlation;
import com.insightml.utils.Arrays;
import com.insightml.utils.Utils;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.IChartGui;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public final class FeaturesCorrelation extends AbstractClass implements IUiProvider<ISamples<?, Double>> {

	public static FeatureCorrelation[] correlation(final ISamples<?, ?> table, final int labelIndex) {
		return Arrays.of(ParallelFor.run(feature -> {
			final double[] feats = new double[table.size()];
			final double[][] features = table.features();
			for (int i = 0; i < feats.length; ++i) {
				feats[i] = features[i][feature];
			}
			final Object[] exp = table.expected(labelIndex);
			final double[] expCast = new double[exp.length];
			for (int i = 0; i < exp.length; ++i) {
				expCast[i] = Utils.toDouble(exp[i]);
			}
			return new FeatureCorrelation(feats, expCast, table.featureNames()[feature]);
		}, 0, table.numFeatures(), 1));
	}

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final ArrayList<FeatureCorrelation> features = Lists.newArrayList(correlation(instances, labelIndex));
		Collections.sort(features);
		final StringBuilder builder = new StringBuilder(512);
		for (final FeatureCorrelation feature : features) {
			builder.append(UiUtils.fill(feature.feature, 40));
			builder.append(feature.getText() + "\n");
		}
		return builder.toString();
	}

	public static void displayGui(final IChartGui gui, final ISamples<?, Double> train, final ISamples<?, Double> test,
			final int labelIndex) {
		for (final FeatureCorrelation feature : correlation(train, labelIndex)) {
			gui.addLineChart(feature.getChart(feature.getFeature()));
		}
		for (final FeatureCorrelation feature : correlation(test, labelIndex)) {
			gui.addLineChart(feature.getChart(feature.getFeature()));
		}
		gui.run();
	}

	public static final class FeatureCorrelation extends Correlation {

		final String feature;

		FeatureCorrelation(final double[] features, final double[] expected, final String featureName) {
			super(features, expected);
			feature = featureName;
		}

		public String getFeature() {
			return feature;
		}
	}

}
