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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
import com.insightml.models.trees.GrowJob;
import com.insightml.models.trees.MseSplitCriterion;
import com.insightml.models.trees.RegTree;
import com.insightml.models.trees.SplitFinderContext;
import com.insightml.models.trees.TreeNode;
import com.insightml.utils.Arrays;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public final class SplitGain implements IFeatureStatistic, IUiProvider<ISamples<?, Double>> {
	private static final Logger LOG = LoggerFactory.getLogger(SplitGain.class);

	private final int maxDepth;
	private final int minObs;

	public SplitGain() {
		this(1, 10);
	}

	public SplitGain(final int maxDepth, final int minObs) {
		this.maxDepth = maxDepth;
		this.minObs = minObs;
	}

	@Override
	public final Map<String, Double> run(final FeatureStatistics stats) {
		final ISamples<Sample, Double> instances = stats.getInstances();
		final int labelIndex = stats.getLabelIndex();

		return run(instances, labelIndex, maxDepth, minObs);
	}

	public static @Nonnull Map<String, Double> run(final ISamples<?, Double> instances, final int labelIndex,
			final int maxDepth, final int minObs) {
		final long start = System.currentTimeMillis();

		final String[] feats = instances.featureNames();
		final Double[] result = Arrays.of(ParallelFor
				.run(i -> Double.valueOf(compute(i, instances, labelIndex, maxDepth, minObs)), 0, feats.length, 1));
		final Map<String, Double> map = new HashMap<>(feats.length);
		for (int i = 0; i < feats.length; ++i) {
			map.put(feats[i], result[i]);
		}

		LOG.info("Computed statistics in {} ms", Long.valueOf(System.currentTimeMillis() - start));
		return map;
	}

	private static double compute(final int feature, final ISamples train, final int labelIndex, final int maxDepth,
			final int minObs) {
		final boolean[] featuresMask = new boolean[train.numFeatures()];
		featuresMask[feature] = true;
		final SplitFinderContext context = new SplitFinderContext(train, featuresMask, maxDepth, minObs, 0, labelIndex);
		final boolean[] subset = RegTree.makeTrainingSubset(train);

		final TreeNode root = RegTree.createTreeRoot(train, labelIndex);
		final String nodePrediction = "mean";
		new GrowJob(root, context, subset, 1, nodePrediction, MseSplitCriterion::create, () -> new Stats(), false)
				.compute();
		return root.featureImportance(true).sumAll();
	}

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		return UiUtils.toString(Collections.sort(run(instances, labelIndex, maxDepth, minObs), SortOrder.DESCENDING),
				true,
				false);
	}
}
