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

import java.io.Serial;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.SimpleStatistics;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.AbstractModel;
import com.insightml.models.DistributionModel;
import com.insightml.models.DistributionPrediction;
import com.insightml.models.DoubleModel;
import com.insightml.models.trees.TreeNode;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.types.DoublePair;
import com.insightml.utils.ui.UiUtils;

public final class BoostingModel extends AbstractModel<Sample, Double> implements DistributionModel<Sample> {
	@Serial
	private static final long serialVersionUID = -8115269534209318613L;

	private DoubleModel first;
	private List<DoublePair<DoubleModel>> steps;

	BoostingModel() {
	}

	public BoostingModel(final DoubleModel first, final List<DoublePair<DoubleModel>> steps, final String[] features) {
		super(features);
		this.first = first;
		this.steps = steps;
	}

	@Override
	public Double[] apply(final ISamples<? extends Sample, ?> instances) {
		final double[] preds = first.predictDouble(instances);
		final float[][] features = instances.features();
		// We do not expect to have step-level feature filtering as of now
		Check.isNull(steps.get(0).getKey().constractFeaturesFilter(instances));
		ParallelFor.run(i -> {
			for (final DoublePair<DoubleModel> step : steps) {
				final double fit = step.getKey().predict(features[i], null);
				preds[i] = updatePrediction(preds[i], fit, step.getValue());
			}
			return 1;
		}, 0, instances.size(), 10_000);
		return Arrays.cast(preds);
	}

	private static double updatePrediction(final double lastModel, final double update, final double gamma) {
		return lastModel + gamma * update;
	}

	@Override
	public String info() {
		if (steps.size() > 1) {
			final DoublePair<DoubleModel> last = steps.get(steps.size() - 1);
			return "First model:\n" + steps.get(1).getKey().info() + "Last model:\n" + last.getKey().info()
					+ "\nFeature importance overall:\n" + UiUtils.format(featureImportance().distribution(), 0);
		}
		return "No training data";
	}

	@Override
	public SumMap<String> featureImportance() {
		final SumMapBuilder<String> builder = SumMap.builder(false);
		for (final DoublePair<DoubleModel> step : steps) {
			for (final Entry<String, Double> imp : step.getKey().featureImportance()) {
				builder.increment(imp.getKey(), imp.getValue());
			}
		}
		return builder.build(0);
	}

	@Override
	public String getName() {
		return MoreObjects.toStringHelper(this).add("first", first).add("steps", steps.size()).toString();
	}

	@Override
	public DistributionPrediction[] predictDistribution(final ISamples<? extends Sample, ?> samples,
			final boolean debug) {
		final float[][] X = samples.features();
		// No step-level feature filtering expected
		Check.isNull(steps.get(0).getKey().constractFeaturesFilter(samples));

		final DistributionPrediction[] out = new DistributionPrediction[X.length];

		ParallelFor.run(i -> {
			out[i] = predictDistribution(X[i], debug);
			return 1;
		}, 0, samples.size(), 10_000);

		return out;
	}

	@Override
	public DistributionPrediction predictDistribution(final float[] x, final boolean debug) {
		double prediction = first instanceof final DistributionModel<?> distributionModel
				? distributionModel.predictDistribution(x, debug).getPrediction().getMean()
				: first.predict(x, null);

		final BoostingPredictionInfo dbg = debug ? new BoostingPredictionInfo() : null;
		if (debug) {
			dbg.featureImpact = new LinkedHashMap<>();
		}

		for (final DoublePair<DoubleModel> step : steps) {
			final DoubleModel model = step.getKey();
			final double weight = step.getValue();

			final DistributionPrediction dp = model instanceof final DistributionModel<?> distributionModel
					? distributionModel.predictDistribution(x, debug)
					: null;

			final double stepMean = dp != null ? dp.getPrediction().getMean() : model.predict(x, null);
			prediction += stepMean * weight;

			if (debug) {
				final Object modelDebug = dp != null ? dp.getDebug() : null;

				// merge per-feature impacts from trees, scaled by gamma
				if (modelDebug instanceof final TreeNode.TreePredictionInfo treePredictionInfo) {
					final Map<String, Double> imp = treePredictionInfo.impactByFeature();
					for (final Map.Entry<String, Double> featureAndImpact : imp.entrySet()) {
						dbg.featureImpact
								.merge(featureAndImpact.getKey(), weight * featureAndImpact.getValue(), Double::sum);
					}
				}
			}
		}

		final SimpleStatistics stats = new SimpleStatistics(1, prediction, prediction, 1);

		if (debug) {
			dbg.featureImpact = sortByAbsDesc(dbg.featureImpact);
		}
		return new DistributionPrediction(stats, debug ? dbg : null);
	}

	private static LinkedHashMap<String, Double> sortByAbsDesc(final Map<String, Double> m) {
		return m.entrySet().stream()
				.sorted(Comparator.comparingDouble((Map.Entry<String, Double> e) -> Math.abs(e.getValue())).reversed()
						.thenComparing(Map.Entry::getKey) // deterministic tie-break
				).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(first, steps, features());
	}

	@Override
	public boolean equals(final Object obj) {
		final BoostingModel oth = (BoostingModel) obj;
		return first.equals(oth.first) && steps.equals(oth.steps);
	}

	public static final class BoostingPredictionInfo {
		LinkedHashMap<String, Double> featureImpact; // aggregated and gamma-scaled

		public Map<String, Double> getFeatureImpact() {
			return featureImpact;
		}
	}

}
