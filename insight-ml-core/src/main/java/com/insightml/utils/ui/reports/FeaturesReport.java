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
		return provider.getClass().getSimpleName()
				+ "\n------------------------------------------------------------------------------------------------\n"
				+ provider.getText(instances, labelIndex);
	}

}
