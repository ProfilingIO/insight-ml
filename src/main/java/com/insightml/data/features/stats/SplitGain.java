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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.Stats;
import com.insightml.models.trees.GrowJob;
import com.insightml.models.trees.MseSplitCriterion;
import com.insightml.models.trees.RegTree;
import com.insightml.models.trees.RegTree.StatsSupplier;
import com.insightml.models.trees.SplitFinderContext;
import com.insightml.models.trees.TreeModel;
import com.insightml.models.trees.TreeNode;
import com.insightml.utils.Arrays;
import com.insightml.utils.Collections;
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
	public Map<String, Double> run(final FeatureStatistics stats) {
		final ISamples<Sample, Double> instances = stats.getInstances();
		final int labelIndex = stats.getLabelIndex();

		return run(instances, labelIndex, maxDepth, minObs).entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().varianceReduction));
	}

	public static @Nonnull Map<String, SplitGainInfo> run(final ISamples<?, Double> instances, final int labelIndex,
			final int maxDepth, final int minObs) {
		final long start = System.currentTimeMillis();

		final Pair<Stats, Stats> labelAndErrorStats = labelAndErrorStats(instances, labelIndex);
		final double totalError = labelAndErrorStats.getSecond().getWeightedSum();

		final String[] feats = instances.featureNames();
		final Map<String, SplitGainInfo> map = new HashMap<>(feats.length);
		for (final SplitGainInfo info : ParallelFor.run(
				i -> compute(i, feats[i], instances, labelIndex, maxDepth, minObs, totalError),
				0,
				feats.length,
				1,
				Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))) {
			map.put(info.featureName, info);
		}

		LOG.info("Computed statistics in {} ms", Long.valueOf(System.currentTimeMillis() - start));
		return map;
	}

	private static Pair<Stats, Stats> labelAndErrorStats(final ISamples<?, Double> instances, final int labelIndex) {
		final Stats labelStats = new Stats();
		final Double[] labels = instances.expected(labelIndex);
		final double[] weights = instances.weights(labelIndex);
		for (int i = 0; i < labels.length; ++i) {
			labelStats.add(labels[i], weights[i]);
		}
		final double prior = labelStats.getMean();
		final Stats errorStats = new Stats();
		for (int i = 0; i < labels.length; ++i) {
			final double error = labels[i] - prior;
			errorStats.add(error * error, weights[i]);
		}
		return new Pair<>(labelStats, errorStats);
	}

	private static SplitGainInfo compute(final int feature, final String featureName, final ISamples train,
			final int labelIndex, final int maxDepth, final int minObs, final double totalError) {
		final boolean[] featuresMask = new boolean[train.numFeatures()];
		featuresMask[feature] = true;
		final SplitFinderContext context = new SplitFinderContext(train, featuresMask, maxDepth, 0, labelIndex);
		final boolean[] subset = RegTree.makeTrainingSubset(train, labelIndex);

		final TreeNode root = RegTree.createTreeRoot(train, labelIndex);
		final String nodePrediction = "mean";
		new GrowJob(root, context, subset, 1, nodePrediction, MseSplitCriterion::create, minObs, new StatsSupplier(),
				false).compute();
		final String rulePresentation = root.getRule() == null ? null : root.getRule().getRulePresentation();
		final TreeNode[] children = root.getChildren();

		final Stats errorStats = new Stats();
		double[] predictions;
		try {
			predictions = new TreeModel(root, train.featureNames()).predictDouble(train);
		} catch (final Exception e) {
			LOG.error("{}", e.getMessage(), e);
			predictions = new double[train.size()];
		}
		for (int i = 0; i < context.expected.length; ++i) {
			final double error = context.expected[i] - predictions[i];
			errorStats.add(error * error, context.weights[i]);
		}
		final double varianceReduction = 1 - errorStats.getWeightedSum() / totalError;

		return new SplitGainInfo(feature, featureName, varianceReduction, rulePresentation,
				children == null ? null : children[0].getMean(), children == null ? null : children[1].getMean());
	}

	@Override
	public String getText(final ISamples<?, Double> instances, final int labelIndex) {
		final StringBuilder text = new StringBuilder();

		final Pair<Stats, Stats> labelAndErrorStats = labelAndErrorStats(instances, labelIndex);
		final double totalError = labelAndErrorStats.getSecond().getWeightedSum();
		text.append("Label stats: " + labelAndErrorStats.getFirst() + '\n');
		text.append("Total variance: " + (float) totalError + "\n\n");

		final SplitGainInfo[] sorted = getRankedSplitGains(instances, labelIndex);
		final Map<String, String> result = new LinkedHashMap<>(sorted.length);
		final double[][] features = instances.features();
		for (int i = 0; i < sorted.length; ++i) {
			final FeatureCorrelation bestCor = findStrongestCorrelation(i, sorted, features);
			final String featureStr = sorted[i].toString();
			result.put(sorted[i].featureName,
					bestCor.bestCor != 0
							? UiUtils.fill(featureStr, 60) + UiUtils.format(bestCor.bestCor) + " corr with "
									+ instances.featureNames()[bestCor.bestCorrFeature]
							: featureStr);
		}

		text.append(UiUtils.toString(result, true, false));

		return text.toString();
	}

	public String getCsv(final ISamples<?, Double> instances, final int labelIndex) {
		final SplitGainInfo[] sorted = getRankedSplitGains(instances, labelIndex);
		final StringBuilder result = new StringBuilder();
		result.append("Feature,Gain,Top rule,Strongest correlation,Strongest correlation feature\n");
		final double[][] features = instances.features();
		for (int i = 0; i < sorted.length; ++i) {
			final FeatureCorrelation bestCor = findStrongestCorrelation(i, sorted, features);
			result.append(sorted[i].featureName + ',');
			result.append(UiUtils.format(sorted[i].varianceReduction) + ',');
			result.append(sorted[i].formatRule());
			if (bestCor.bestCor > 0) {
				result.append(',' + UiUtils.format(bestCor.bestCor) + ',');
				result.append(instances.featureNames()[bestCor.bestCorrFeature]);
			}
			result.append('\n');
		}
		return result.toString();
	}

	private SplitGainInfo[] getRankedSplitGains(final ISamples<?, Double> instances, final int labelIndex) {
		return Arrays.of(Collections.sortDesc(run(instances, labelIndex, maxDepth, minObs)).values(),
				SplitGainInfo.class);
	}

	private static FeatureCorrelation findStrongestCorrelation(final int i, final SplitGainInfo[] sorted,
			final double[][] features) {
		final FeatureCorrelation bestCor = new FeatureCorrelation();
		if (i < 50) {
			for (int j = 0; j < i; ++j) {
				try {
					final double[] fi = new double[features.length];
					final double[] fj = new double[features.length];
					for (int s = 0; s < fi.length; ++s) {
						fi[s] = features[s][sorted[i].featureIndex];
						fj[s] = features[s][sorted[j].featureIndex];
					}
					final double corr = new PearsonsCorrelation().correlation(fi, fj);
					if (corr > bestCor.bestCor) {
						bestCor.bestCor = corr;
						bestCor.bestCorrFeature = sorted[j].featureIndex;
					}
				} catch (final Exception e) {
					LOG.error("{} for {} vs {}", e.getMessage(), sorted[i].featureName, sorted[j].featureName, e);
				}
			}
		}
		return bestCor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(maxDepth, minObs);
	}

	private static final class FeatureCorrelation {
		private double bestCor = 0;
		private int bestCorrFeature = 0;
	}

	public static final class SplitGainInfo implements Comparable<SplitGainInfo> {
		private final int featureIndex;
		private final String featureName;

		private final double varianceReduction;
		private final String rule;
		private final Double predictionLeft;
		private final Double predictionRight;

		public SplitGainInfo(final int featureIndex, final String featureName, final double varianceReduction,
				final String rule, final Double predictionLeft, final Double predictionRight) {
			this.featureIndex = featureIndex;
			this.featureName = featureName;
			this.varianceReduction = varianceReduction;
			this.rule = rule;
			this.predictionLeft = predictionLeft;
			this.predictionRight = predictionRight;
		}

		public double getVarianceReduction() {
			return varianceReduction;
		}

		public String formatRule() {
			return rule == null ? ""
					: rule + " -> " + UiUtils.format(predictionLeft) + "; else " + UiUtils.format(predictionRight);
		}

		@Override
		public int compareTo(final SplitGainInfo o) {
			final int comp = Double.compare(varianceReduction, o.varianceReduction);
			if (comp != 0) {
				return comp;
			}
			return featureName.compareTo(o.featureName);
		}

		@Override
		public String toString() {
			return UiUtils.fill(UiUtils.format(varianceReduction), 12) + formatRule();
		}
	}
}
