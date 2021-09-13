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
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISamples;
import com.insightml.math.statistics.Correlation;
import com.insightml.utils.Utils;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.IChartGui;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public final class FeaturesCorrelation extends AbstractClass implements IUiProvider<ISamples<?, Double>> {
	private static final Logger LOG = LoggerFactory.getLogger(FeaturesCorrelation.class);

	public static void correlation(final ISamples<?, ?> samples, final int labelIndex,
			final Consumer<FeatureCorrelation> consumer) {
		final long start = System.currentTimeMillis();
		final int numFeatures = samples.numFeatures();
		for (int feature = 0; feature < numFeatures; ++feature) {
			consumer.accept(calculateFeatureCorrelation(samples, labelIndex, feature));
		}
		LOG.info("Computed correlation in {} ms", Long.valueOf(System.currentTimeMillis() - start));
	}

	private static FeatureCorrelation calculateFeatureCorrelation(final ISamples<?, ?> samples, final int labelIndex,
			final int feature) {
		final float[] feats = new float[samples.size()];
		final float[][] features = samples.features();
		for (int i = 0; i < feats.length; ++i) {
			feats[i] = features[i][feature];
		}
		final Object[] exp = samples.expected(labelIndex);
		final float[] expCast = new float[exp.length];
		for (int i = 0; i < exp.length; ++i) {
			expCast[i] = Utils.toFloat(exp[i]);
		}
		return new FeatureCorrelation(feats, expCast, samples.featureNames()[feature]);
	}

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final ArrayList<FeatureCorrelation> features = new ArrayList<>();
		correlation(instances, labelIndex, features::add);
		Collections.sort(features);
		final StringBuilder builder = new StringBuilder(512);
		for (final FeatureCorrelation feature : features) {
			builder.append(UiUtils.fill(feature.getFeature(), 40));
			builder.append(feature.getText() + "\n");
		}
		return builder.toString();
	}

	public static String getCsv(final ISamples<?, Double> instances, final int labelIndex) {
		final ArrayList<FeatureCorrelation> features = new ArrayList<>();
		correlation(instances, labelIndex, features::add);
		Collections.sort(features);
		final StringBuilder builder = new StringBuilder(512);
		builder.append("Feature,Covariance,Pearson,Spearman\n");
		for (final FeatureCorrelation feature : features) {
			builder.append(feature.getFeature() + ',');
			builder.append(UiUtils.format(feature.getCovariance()) + ',');
			builder.append(UiUtils.format(feature.getPearson()) + ',');
			builder.append(UiUtils.format(feature.getSpearman()) + '\n');
		}
		return builder.toString();
	}

	public static void displayGui(final IChartGui gui, final ISamples<?, Double> train, final ISamples<?, Double> test,
			final int labelIndex) {
		correlation(train, labelIndex, feature -> gui.addLineChart(feature.getChart(feature.getFeature())));
		correlation(test, labelIndex, feature -> gui.addLineChart(feature.getChart(feature.getFeature())));
		gui.run();
	}

	public static final class FeatureCorrelation extends Correlation {
		private final String feature;

		FeatureCorrelation(final float[] features, final float[] expected, final String featureName) {
			super(features, expected);
			feature = featureName;
		}

		public String getFeature() {
			return feature;
		}
	}

}
