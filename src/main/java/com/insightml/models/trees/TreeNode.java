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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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

	public Split getRule() {
		return rule;
	}

	public TreeNode[] getChildren() {
		return children;
	}

	public IStats getStats() {
		return stats;
	}

	public double getMean() {
		return mean;
	}

	public DistributionPrediction predictDistribution(final double[] features, final boolean debug) {
		if (!debug) {
			for (TreeNode node = this;; node = node.rule.selectChild(features, node.children)) {
				if (node.rule == null) {
					return new DistributionPrediction(node.stats, null);
				}
			}
		}
		final List<TreeDecisionDebug> appliedRules = new ArrayList<>();
		final Map<String, Double> impactByFeature = new HashMap<>();
		for (TreeNode node = this;;) {
			final Split nodeRule = node.rule;
			if (nodeRule == null) {
				return new DistributionPrediction(node.stats, new TreePredictionInfo(appliedRules, impactByFeature));
			}
			final TreeNode child = nodeRule.selectChild(features, node.children);
			appliedRules.add(new TreeDecisionDebug(nodeRule, child, features));
			impactByFeature.merge(nodeRule.getFeatureName(), child.mean - node.mean, Double::sum);
			node = child;
		}
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
		return calculateSplit(ordered, subset, indexNaN, index);
	}

	public static boolean[][] calculateSplit(final int[] ordered, final boolean[] subset, final int indexNaN,
			final int index) {
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

	public List<Pair<List<String>, IStats>> collectSegments() {
		final List<Pair<List<String>, IStats>> segments = new ArrayList<>();
		collectSegments(ImmutableList.of(), segments);
		return segments.stream().sorted((e1, e2) -> Double.compare(e2.getValue().getMean(), e1.getValue().getMean()))
				.collect(Collectors.toList());
	}

	private void collectSegments(final List<String> parentSegment, final List<Pair<List<String>, IStats>> segments) {
		if (rule != null) {
			final String fName = rule.getFeatureName();
			final String threshold = UiUtils.format(rule.getFeatureValueThreshold());
			final List<String> segmentLeft = new ArrayList<>(parentSegment);
			segmentLeft.add(fName + " \u2264 " + threshold);
			children[0].collectSegments(segmentLeft, segments);
			final List<String> segmentRight = new ArrayList<>(parentSegment);
			segmentRight.add(fName + " > " + threshold);
			children[1].collectSegments(segmentRight, segments);
			if (children.length > 2) {
				final List<String> segmentNaN = new ArrayList<>(parentSegment);
				segmentNaN.add(fName + " missing");
				children[2].collectSegments(segmentNaN, segments);
			}
		} else {
			segments.add(new Pair<>(parentSegment, stats));
		}
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

	public SumMap<String> collectPositiveFactors() {
		final SumMapBuilder<String> builder = SumMap.builder(false);
		collectFactors(builder, true);
		return builder.build(0);
	}

	public SumMap<String> collectNegativeFactors() {
		final SumMapBuilder<String> builder = SumMap.builder(false);
		collectFactors(builder, false);
		return builder.build(0);
	}

	private void collectFactors(final SumMapBuilder<String> builder, final boolean positive) {
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				final TreeNode child = children[i];
				if (positive && child.mean > mean) {
					builder.increment(representSplit(i), child.mean - mean);
				} else if (!positive && child.mean < mean) {
					builder.increment(representSplit(i), mean - child.mean);
				}
				child.collectFactors(builder, positive);
			}
		}
	}

	private String representSplit(final int child) {
		final String fName = rule.getFeatureName();
		if (child == 2) {
			return fName + " missing";
		}
		final String threshold = true ? "X" : UiUtils.format(rule.getFeatureValueThreshold());
		return fName + (child == 0 ? " \u2264 " : " > ") + threshold;
	}

	public String info() {
		final StringBuilder builder = new StringBuilder(128);
		builder.append('\n');
		print("", true, builder);
		builder.append("\nFeature importance:\n");
		builder.append(UiUtils
				.toString(Collections.sort(featureImportance(false).getMap(), SortOrder.DESCENDING), true, true));
		builder.append("\nFeature segments:\n" + UiUtils.format(collectSegments()) + "\n");
		builder.append("\nPositive factors:\n"
				+ UiUtils.toString(Collections.sortDesc(collectPositiveFactors().distribution().getMap()), true, true)
				+ "\n");
		builder.append("\nNegative factors:\n"
				+ UiUtils.toString(Collections.sortDesc(collectNegativeFactors().distribution().getMap()), true, true)
				+ "\n");
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

	public static String presentPrediction(final IStats stats) {
		final Double stddev = getStandardDeviation(stats);
		return UiUtils.format(stats.getMean()) + (stddev == null ? "" : " +/- " + UiUtils.format(stddev)) + " ("
				+ UiUtils.format(stats.getSumOfWeights()) + ")";
	}

	private static Double getStandardDeviation(final IStats stats) {
		try {
			return stats.getStandardDeviation();
		} catch (final UnsupportedOperationException e) {
			return null;
		}
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

	@Override
	public int hashCode() {
		return Objects.hash(rule, Arrays.deepHashCode(children), mean);
	}

	public static final class TreePredictionInfo {
		private final List<TreeDecisionDebug> appliedRules;
		private final Map<String, Double> impactByFeature;

		public TreePredictionInfo(final List<TreeDecisionDebug> appliedRules,
				final Map<String, Double> impactByFeature) {
			this.appliedRules = appliedRules;
			this.impactByFeature = impactByFeature;
		}

		public List<TreeDecisionDebug> getAppliedRules() {
			return appliedRules;
		}

		public Map<String, Double> getImpactByFeature() {
			return impactByFeature;
		}

		@Override
		public String toString() {
			return com.google.common.base.Objects.toStringHelper(this).add("appliedRules", appliedRules)
					.add("impactByFeature", impactByFeature).toString();
		}
	}

	public static final class TreeDecisionDebug {
		private final Split nodeRule;
		private final TreeNode child;
		private final double[] features;

		public TreeDecisionDebug(final Split nodeRule, final TreeNode child, final double[] features) {
			this.nodeRule = nodeRule;
			this.child = child;
			this.features = features;
		}

		public Split getNodeRule() {
			return nodeRule;
		}

		public TreeNode getChild() {
			return child;
		}

		public double[] getFeatures() {
			return features;
		}

		public String getPresentation() {
			return nodeRule.explain(features) + " \u2192 " + presentPrediction(child.stats);
		}

		@Override
		public String toString() {
			return getPresentation();
		}
	}
}
