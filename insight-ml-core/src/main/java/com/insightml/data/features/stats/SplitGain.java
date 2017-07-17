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

import com.insightml.models.trees.ISplit;
import com.insightml.models.trees.MseSplitCriterion;
import com.insightml.models.trees.SplitFinderContext;
import com.insightml.models.trees.ThresholdSplitFinder;

public final class SplitGain extends AbstractIndependentFeatureStatistic {

	@Override
	protected double compute(final FeatureStatistics stats, final int feature, final String featureName) {
		final SplitFinderContext context = new SplitFinderContext(stats.getInstances(), 10, 10, stats.getLabelIndex());
		final boolean[] subset = new boolean[stats.getInstances().size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		final ISplit split = ThresholdSplitFinder.createThresholdSplitFinder(context, subset, MseSplitCriterion::create)
				.apply(feature);
		return split == null ? 0 : split.getImprovement() / split.getWeightSum();
	}
}
