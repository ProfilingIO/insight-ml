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

import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.AbstractModel;
import com.insightml.models.DoubleModel;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.jobs.ParallelFor;
import com.insightml.utils.types.DoublePair;
import com.insightml.utils.ui.UiUtils;

public final class BoostingModel extends AbstractModel<Sample, Double> {

	private static final long serialVersionUID = -8115269534209318613L;

	private DoubleModel first;
	private List<DoublePair<DoubleModel>> steps;

	BoostingModel() {
	}

	public BoostingModel(final DoubleModel first, final List<DoublePair<DoubleModel>> steps) {
		super(null);
		this.first = first;
		this.steps = steps;
	}

	@Override
	public Double[] apply(final ISamples<? extends Sample, ?> instances) {
		final double[] preds = first.predictDouble(instances);
		final double[][] features = instances.features();
		// We do not expect to have step-level feature filtering as of now
		Check.isNull(steps.get(0).getKey().constractFeaturesFilter(instances));
		ParallelFor.run(i -> {
			for (final DoublePair<DoubleModel> step : steps) {
				final double fit = step.getKey().predict(features[i], null);
				preds[i] = GBM.updatePrediction(preds[i], fit, step.getValue());
			}
			return 1;
		}, 0, instances.size(), 10_000);
		return Arrays.cast(preds);
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
	public String info() {
		if (steps.size() > 1) {
			final DoublePair<DoubleModel> last = steps.get(steps.size() - 1);
			return "First model:\n" + steps.get(1).getKey().info() + "Last model:\n" + last.getKey().info()
					+ "\nFeature importance overall:\n" + UiUtils.format(featureImportance().distribution(), 0);
		}
		return "No training data";
	}

	@Override
	public boolean equals(final Object obj) {
		final BoostingModel oth = (BoostingModel) obj;
		return first.equals(oth.first) && steps.equals(oth.steps);
	}

	@Override
	public String getName() {
		return Objects.toStringHelper(this).add("first", first).add("steps", steps.size()).toString();
	}

}