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

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.decorators.FeaturesFilterDecorator;
import com.insightml.math.types.SumMap;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerInput;

public class LearnerBasedFeatureStatistics extends AbstractIndependentFeatureStatistic {
	private final ILearner<Sample, Double, Double> learner;

	public LearnerBasedFeatureStatistics(final ILearner<Sample, Double, Double> learner) {
		this.learner = learner;
	}

	@Override
	protected double compute(final FeatureStatistics stats, final int feature, final String featureName) {
		final ISamples<Sample, Double> samples = stats.getInstances();
		final boolean[] featuresMask = new boolean[samples.numFeatures()];
		for (int i = 0; i < featuresMask.length; ++i) {
			featuresMask[i] = i == feature;
		}
		final SumMap<String> featureImportance = learner
				.run(new LearnerInput<>(new FeaturesFilterDecorator<>(samples, featuresMask), 0)).featureImportance();
		final Number imp = featureImportance.get(featureName);
		return imp == null ? 0 : imp.doubleValue();
	}

}
