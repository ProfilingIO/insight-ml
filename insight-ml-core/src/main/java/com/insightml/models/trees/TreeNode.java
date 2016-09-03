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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.insightml.data.samples.ISamples;
import com.insightml.math.statistics.Stats;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.AbstractDoubleLearner;
import com.insightml.models.AbstractIndependentFeaturesModel;
import com.insightml.models.DistributionPrediction;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.UiUtils;

public final class TreeNode extends AbstractClass implements Serializable {
	private static final long serialVersionUID = -4612424838699629485L;

	private ISplit rule;
	private TreeNode left;
	private TreeNode right;

	private Stats stats;
	private AbstractIndependentFeaturesModel model;

	TreeNode() {
	}

	TreeNode(final Stats stats) {
		this.stats = stats;
	}

	public double predict(final double[] features) {
		if (rule == null) {
			return model == null ? stats.getMean() : model.predict(features) * 0.5 + stats.getMean() * 0.5;
		}
		return rule.moveRight(features) ? right.predict(features) : left.predict(features);
	}

	public DistributionPrediction predictDistribution(final double[] features, final boolean debug) {
		if (!debug) {
			return new DistributionPrediction(predictDistributionNoDebug(features), null);
		}
		if (rule == null) {
			return new DistributionPrediction(stats, null);
		}
		final boolean moveRight = rule.moveRight(features);
		final DistributionPrediction pred = moveRight ? right.predictDistribution(features, debug)
				: left.predictDistribution(features, debug);
		return new DistributionPrediction(pred.getPrediction(),
				debug ? makeDebugOutput(features, moveRight, pred) : null);
	}

	private Stats predictDistributionNoDebug(final double[] features) {
		if (rule == null) {
			return stats;
		}
		final boolean moveRight = rule.moveRight(features);
		return moveRight ? right.predictDistributionNoDebug(features) : left.predictDistributionNoDebug(features);
	}

	private List<String> makeDebugOutput(final double[] features, final boolean moveRight,
			final DistributionPrediction pred) {
		final List<String> debugValue = Lists.newArrayList(
				rule.explain(features) + " \u2192 " + presentPrediction(moveRight ? right.stats : left.stats));
		final Object childDebug = pred.getDebug();
		if (childDebug != null) {
			debugValue.addAll((Collection<? extends String>) childDebug);
		}
		return debugValue;
	}

	Pair<boolean[], boolean[]> split(final ISplit split, final TreeNode leftNode, final TreeNode rightNode,
			final int[][] orderedIndexes, final boolean[] subset) {
		rule = Preconditions.checkNotNull(split);
		left = leftNode;
		right = rightNode;

		return calculateSplit(split, orderedIndexes, subset);
	}

	public static Pair<boolean[], boolean[]> calculateSplit(final ISplit split, final int[][] orderedIndexes,
			final boolean[] subset) {
		final int[] ordered = orderedIndexes[split.getFeature()];
		final int index = split.getLastIndexLeft();
		final boolean[] leftI = new boolean[subset.length];
		final boolean[] rightI = new boolean[subset.length];
		for (int i = 0; i < ordered.length; ++i) {
			final int idx = ordered[i];
			if (subset[idx]) {
				if (i <= index) {
					leftI[idx] = true;
				} else {
					rightI[idx] = true;
				}
			}
		}
		return new Pair<>(leftI, rightI);
	}

	public void setLeafModel(final AbstractDoubleLearner learner, final ISamples<?, Double> instances) {
		model = (AbstractIndependentFeaturesModel) learner.run(new LearnerInput<>(instances, null, null, 0));
	}

	public SumMap<String> featureImportance(final boolean normalize) {
		final SumMapBuilder<String> sum = SumMap.builder(false);
		importance(this, sum, normalize);
		return sum.build(0);
	}

	private void importance(final TreeNode node, final SumMapBuilder<String> sum, final boolean normalize) {
		final ISplit crit = node.rule;
		if (crit != null) {
			sum.increment(crit.getFeatureName(),
					normalize ? crit.getImprovement() / crit.getWeightSum() : crit.getImprovement());
			importance(node.left, sum, normalize);
			importance(node.right, sum, normalize);
		}
	}

	public String info() {
		final StringBuilder builder = new StringBuilder(128);
		builder.append('\n');
		print("", true, builder);
		for (final boolean bool : new boolean[] { true, false }) {
			builder.append('\n');
			builder.append(UiUtils.toString(Collections.sort(featureImportance(bool).getMap(), SortOrder.DESCENDING),
					true,
					true));
		}
		return builder.toString();
	}

	private void print(final String prefix, final boolean isTail, final StringBuilder builder) {
		builder.append(prefix + (isTail ? "└── " : "├── ") + (rule != null ? rule + " / " : "")
				+ presentPrediction(stats) + "\n");
		if (rule != null) {
			left.print(prefix + (isTail ? "    " : "│   "), false, builder);
			right.print(prefix + (isTail ? "    " : "│   "), true, builder);
		}
	}

	private static String presentPrediction(final Stats stats) {
		return UiUtils.format(stats.getMean()) + " +/- " + UiUtils.format(stats.getStandardDeviation()) + " ("
				+ UiUtils.format(stats.getSumOfWeights()) + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		final TreeNode oth = (TreeNode) obj;
		if (stats.getMean() != oth.stats.getMean()) {
			return false;
		}
		// TODO: deeper equals
		return stats.getSumOfWeights() == oth.stats.getSumOfWeights();
	}

}
