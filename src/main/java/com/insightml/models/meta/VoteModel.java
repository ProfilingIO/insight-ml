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
package com.insightml.models.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.distributions.DiscreteDistribution;
import com.insightml.math.statistics.FullStatistics;
import com.insightml.math.statistics.FullStatisticsBuilder;
import com.insightml.math.statistics.IStats;
import com.insightml.math.statistics.MutableStatsBuilder;
import com.insightml.math.statistics.Stats;
import com.insightml.math.statistics.StatsBuilder;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.DistributionModel;
import com.insightml.models.DistributionPrediction;
import com.insightml.models.IModel;
import com.insightml.models.trees.TreeModel;
import com.insightml.models.trees.TreeNode.TreeDecisionDebug;
import com.insightml.models.trees.TreeNode.TreePredictionInfo;
import com.insightml.utils.Arrays;
import com.insightml.utils.Collections;
import com.insightml.utils.ui.UiUtils;

public final class VoteModel<I extends Sample> extends AbstractEnsembleModel<I, Double>
		implements DistributionModel<I> {

	private static final long serialVersionUID = -8515840219123634452L;

	public enum VoteStrategy {
		AVERAGE, MEDIAN, GEOMETRIC, HARMONIC
	}

	private VoteStrategy strategy;

	VoteModel() {
	}

	public VoteModel(final IModel<I, Double>[] models, final double[] weights, final VoteStrategy strategy,
			final String[] features) {
		super(models, weights, features);
		this.strategy = strategy;
	}

	@Override
	public Double[] apply(final ISamples<? extends I, ?> instnces) {
		final IModel<I, Double>[] models = getModels();
		final Double[][] predss = new Double[models.length][];
		for (int m = 0; m < models.length; ++m) {
			predss[m] = models[m].apply(instnces);
		}
		return ensemble(getWeights(), predss, strategy);
	}

	public static Double[] ensemble(final double[] weights, final Double[][] predss, final VoteStrategy strategy) {
		final Stats[] map = Arrays.fill(predss[0].length, Stats.class);
		for (int i = 0; i < predss.length; ++i) {
			for (int j = 0; j < predss[i].length; ++j) {
				map[j].add(predss[i][j], weights[i]);
			}
		}
		final Double[] preds = new Double[map.length];
		for (int i = 0; i < preds.length; ++i) {
			preds[i] = resolve(map[i], strategy);
		}
		return preds;
	}

	@Override
	public DistributionPrediction[] predictDistribution(final ISamples<? extends I, ?> instnces, final boolean debug) {
		final StatsBuilder<?>[] map = new StatsBuilder<?>[instnces.size()];
		final VoteModelDebug[] debg = new VoteModelDebug[map.length];
		for (final IModel<I, Double> model : getModels()) {
			final DistributionPrediction[] preds = ((DistributionModel<I>) model).predictDistribution(instnces, debug);
			for (int j = 0; j < preds.length; ++j) {
				final IStats prediction = preds[j].getPrediction();
				if (map[j] == null) {
					// we can only aggregate to full statistics if also the base model predictions
					// are full statistics
					map[j] = prediction instanceof FullStatistics ? new FullStatisticsBuilder()
							: new MutableStatsBuilder<>(new Stats());
					debg[j] = new VoteModelDebug();
				}
				map[j].add(prediction);
				debg[j].add(preds[j].getDebug());
			}
		}
		final DistributionPrediction[] result = new DistributionPrediction[map.length];
		for (int i = 0; i < map.length; ++i) {
			result[i] = new DistributionPrediction(map[i].create(), debg[i]);
		}
		return result;
	}

	private static double resolve(final Stats stats, final VoteStrategy strategy) {
		switch (strategy) {
		case AVERAGE:
			return stats.getMean();
		case MEDIAN:
			// return stats.getPercentile(50);
		case GEOMETRIC:
			// return stats.getGeometricMean();
		case HARMONIC:
			// double sum = 0;
			// for (final double value : stats.getValues()) {
			// sum += 1 / value;
			// }
			// return stats.getN() * 1.0 / sum;
		default:
			throw new IllegalArgumentException();
		}
	}

	public DiscreteDistribution<String> collectPositiveFactors() {
		if (!allModelsAreTrees()) {
			return null;
		}
		final SumMapBuilder<String> positive = SumMap.builder(false);
		for (final IModel<I, Double> model : getModels()) {
			positive.incAll(((TreeModel) model).getRoot().collectPositiveFactors().getMap().entrySet());
		}
		return positive.build(0).distribution();
	}

	public DiscreteDistribution<String> collectNegativeFactors() {
		if (!allModelsAreTrees()) {
			return null;
		}
		final SumMapBuilder<String> negative = SumMap.builder(false);
		for (final IModel<I, Double> model : getModels()) {
			negative.incAll(((TreeModel) model).getRoot().collectNegativeFactors().getMap().entrySet());
		}
		return negative.build(0).distribution();
	}

	private boolean allModelsAreTrees() {
		for (final IModel<I, Double> model : getModels()) {
			if (!(model instanceof TreeModel)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String info() {
		final StringBuilder builder = new StringBuilder();
		final SumMap<String> importance = featureImportance();
		builder.append("Feature importance:\n" + UiUtils.format(importance.distribution(), 0).toString());

		if (allModelsAreTrees()) {
			final int n = 20;
			builder.append("\nTop " + n + " positive factors:\n"
					+ UiUtils.toString(Collections.getTopN(collectPositiveFactors().getMap(), n, 0), true, true));
			builder.append("\nTop " + n + " negative factors:\n"
					+ UiUtils.toString(Collections.getTopN(collectNegativeFactors().getMap(), n, 0), true, true));
		}

		return builder.toString();
	}

	public static final class VoteModelDebug {
		private final Map<String, Double> impactByFeature = new HashMap<>();
		private final List<Object> singleModelDebug = new ArrayList<>();

		public Map<String, Double> getImpactByFeature() {
			final TreeMap<String, Double> sortedFeatures = new TreeMap<>(
					(k1, k2) -> Double.compare(Math.abs(impactByFeature.get(k2)), Math.abs(impactByFeature.get(k1))));
			sortedFeatures.putAll(impactByFeature);
			return sortedFeatures;
		}

		public void add(final Object debug) {
			if (debug instanceof TreePredictionInfo) {
				for (final Entry<String, Double> feature : ((TreePredictionInfo) debug).getImpactByFeature()
						.entrySet()) {
					impactByFeature.merge(feature.getKey(), feature.getValue(), Double::sum);
				}
				singleModelDebug.add(((TreePredictionInfo) debug).getAppliedRules());
			} else if (debug != null) {
				singleModelDebug.add(debug);
			}
		}

		public List<Object> getSingleModelDebug() {
			return singleModelDebug;
		}

		@Override
		public String toString() {
			final StringBuilder str = new StringBuilder(getImpactByFeature().toString());
			for (final Object debug : singleModelDebug) {
				str.append('\n');
				if (debug instanceof Collection) {
					str.append(((Collection) debug).stream()
							.map(e -> e instanceof TreeDecisionDebug ? ((TreeDecisionDebug) e).getPresentation() : e)
							.collect(Collectors.joining(", ")));
				} else {
					str.append(debug);
				}
			}
			return str.toString();
		}
	}
}