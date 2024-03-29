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
package com.insightml.models.trees;

import java.util.Arrays;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.types.SumMap;
import com.insightml.models.AbstractIndependentFeaturesModel;
import com.insightml.models.DistributionModel;
import com.insightml.models.DistributionPrediction;

public final class TreeModel extends AbstractIndependentFeaturesModel implements DistributionModel<Sample> {
	private static final long serialVersionUID = -1127329976938652612L;

	private TreeNode root;

	TreeModel() {
	}

	public TreeModel(final TreeNode root, final String[] features) {
		super(features);
		this.root = root;
	}

	public TreeNode getRoot() {
		return root;
	}

	@Override
	public double predict(final float[] features) {
		TreeNode node = root;
		for (;;) {
			if (node.rule == null) {
				return node.mean;
			}
			final TreeNode child = node.rule.selectChild(features, node.children);
			// For missing values there might not be a suitable child
			if (child == null) {
				return node.mean;
			}
			node = child;
		}
	}

	public DistributionPrediction predictDistribution(final float[] features, final boolean debug) {
		return root.predictDistribution(features, debug);
	}

	@Override
	public DistributionPrediction[] predictDistribution(final ISamples<? extends Sample, ?> instances,
			final boolean debug) {
		final int[] featuresFilter = constractFeaturesFilter(instances);
		final float[][] instancesFeatures = instances.features();
		final DistributionPrediction[] predictions = new DistributionPrediction[instancesFeatures.length];
		for (int i = 0; i < predictions.length; ++i) {
			predictions[i] = root.predictDistribution(featuresFilter == null ? instancesFeatures[i]
					: selectFeatures(instancesFeatures[i], featuresFilter), debug);
		}
		return predictions;
	}

	@Override
	public SumMap<String> featureImportance() {
		return root.featureImportance(true);
	}

	@Override
	public String info() {
		return root.info();
	}

	@Override
	public boolean equals(final Object obj) {
		return Arrays.deepEquals(features(), ((TreeModel) obj).features()) && root.equals(((TreeModel) obj).root);
	}

	@Override
	public int hashCode() {
		return root.hashCode();
	}
}
