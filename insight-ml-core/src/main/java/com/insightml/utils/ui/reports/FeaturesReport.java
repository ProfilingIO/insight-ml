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
package com.insightml.utils.ui.reports;

import com.insightml.data.features.stats.FeaturesCorrelation;
import com.insightml.data.features.stats.FeaturesDistribution;
import com.insightml.data.features.stats.FeaturesImportance;
import com.insightml.data.features.stats.FeaturesSummary;
import com.insightml.data.features.stats.SplitGain;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.utils.pipeline.IPipelineElement;
import com.insightml.utils.types.AbstractModule;

public final class FeaturesReport extends AbstractModule
		implements IPipelineElement<ISamples<? extends Sample, Double>, String> {

	private static final long serialVersionUID = 8255879220226883789L;

	private final int labelIndex;

	public FeaturesReport(final int labelIndex) {
		this.labelIndex = labelIndex;
	}

	@Override
	public String run(final ISamples<?, Double> instances) {
		final StringBuilder builder = new StringBuilder(1024);
		builder.append(instances.size() + "\n\n");

		if (instances.numFeatures() <= 1000) {
			builder.append(append(new FeaturesSummary(), instances) + "\n\n");
		}
		if (false) {
			builder.append(append(new FeaturesImportance(), instances) + "\n\n");
		}
		if (instances.numLabels() > 0) {
			if (instances.numFeatures() < 300) {
				builder.append(append(new SplitGain(), instances) + "\n\n");
			}
			builder.append(append(new FeaturesCorrelation(), instances) + "\n\n");
		}
		if (instances.numFeatures() < 300) {
			builder.append(append(new FeaturesDistribution(), instances));
		}

		return builder.toString();
	}

	private String append(final IUiProvider provider, final ISamples<?, Double> instances) {
		final String reportClass = provider.getClass().getSimpleName();
		return reportClass + getLegendWithLB(true, reportClass)
				+ "\n------------------------------------------------------------------------------------------------\n"
				+ provider.getText(instances, labelIndex);
	}

	private static String getLegendWithLB(final boolean showLegend, final String reportClass) {
		final String res = showLegend ? getLegend(reportClass) : null;
		return res == null ? "" : "\n" + res + "\n";
	}

	private static String getLegend(final String reportClass) {
		switch (reportClass) {
		case "FeaturesSummary":
			return "Every row contains one feature, the min/max number of times this feature was seen per sample, how often the feature was null, the median and the mean of the feature over all samples and how many distinct values we have seen.";
		case "FeaturesImportance":
			return null;
		case "SplitGain":
			return "Every row contains one feature, the value shows how good a feature would be to split your data into 2 groups, one having it and one not having it. The higher the better the split would be.";
		case "FeaturesCorrelation":
			return "Every row contains one feature, the correlation (Spearman/Pearson) show how good your feature on its own is to predicts the results. Values close to +/-1 are good.";
		case "FeaturesDistribution":
			return "Work in progress ...";
		default:
			return null;
		}
	}

}
