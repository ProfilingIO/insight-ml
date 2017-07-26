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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.insightml.math.statistics.IStats;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.DistributionPrediction;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.UiUtils;

public final class TreeNode extends AbstractClass implements Serializable {
	private static final long serialVersionUID = -4612424838699629485L;

	Split rule;
	TreeNode[] children;

	double mean;
	IStats stats;

	TreeNode() {
	}

	TreeNode(final double prediction, final IStats stats) {
		this.stats = stats;
		mean = prediction;
	}

	public DistributionPrediction predictDistribution(final double[] features, final boolean debug) {
		if (!debug) {
			return new DistributionPrediction(predictDistributionNoDebug(features), null);
		}
		if (rule == null) {
			return new DistributionPrediction(stats, null);
		}
		final int moveRight = rule.selectChild(features);
		final DistributionPrediction pred = children[moveRight].predictDistribution(features, debug);
		return new DistributionPrediction(pred.getPrediction(),
				debug ? makeDebugOutput(features, moveRight, pred) : null);
	}

	private IStats predictDistributionNoDebug(final double[] features) {
		if (rule == null) {
			return stats;
		}
		return children[rule.selectChild(features)].predictDistributionNoDebug(features);
	}

	private List<String> makeDebugOutput(final double[] features, final int moveRight,
			final DistributionPrediction pred) {
		final List<String> debugValue = Lists
				.newArrayList(rule.explain(features) + " \u2192 " + presentPrediction(children[moveRight].stats));
		final Object childDebug = pred.getDebug();
		if (childDebug != null) {
			debugValue.addAll((Collection<? extends String>) childDebug);
		}
		return debugValue;
	}

	boolean[][] split(final Split split, final TreeNode[] childrenn, final int[][] orderedIndexes,
			final boolean[] subset) {
		rule = Preconditions.checkNotNull(split);
		children = childrenn;

		return calculateSplit(split, orderedIndexes, subset);
	}

	public static boolean[][] calculateSplit(final Split split, final int[][] orderedIndexes, final boolean[] subset) {
		final int[] ordered = orderedIndexes[split.getFeature()];
		final int indexNaN = split.getLastIndexNaN();
		final int index = split.getLastIndexLeft();
		final boolean[][] splits = new boolean[3][subset.length];
		for (int i = 0; i < ordered.length; ++i) {
			final int idx = ordered[i];
			if (subset[idx]) {
				if (i <= indexNaN) {
					splits[2][idx] = true;
				} else if (i <= index) {
					splits[0][idx] = true;
				} else {
					splits[1][idx] = true;
				}
			}
		}
		return splits;
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
			for (final TreeNode child : node.children) {
				importance(child, sum, normalize);
			}
		}
	}

	public String info() {
		final StringBuilder builder = new StringBuilder(128);
		builder.append('\n');
		print("", true, builder);
		for (final boolean bool : new boolean[] { true, false }) {
			builder.append('\n');
			builder.append(UiUtils.toString(Collections.sort(featureImportance(bool).getMap(), SortOrder.DESCENDING),
					true, true));
		}
		return builder.toString();
	}

	private void print(final String prefix, final boolean isTail, final StringBuilder builder) {
		builder.append(prefix + (isTail ? "└── " : "├── ") + (rule != null ? rule + " / " : "")
				+ presentPrediction(stats) + "\n");
		if (rule != null) {
			for (int i = 0; i < children.length; ++i) {
				if (children[i] != null) {
					children[i].print(prefix + (isTail ? "    " : "│   "), i == children.length - 1, builder);
				}
			}
		}
	}

	private static String presentPrediction(final IStats stats) {
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
