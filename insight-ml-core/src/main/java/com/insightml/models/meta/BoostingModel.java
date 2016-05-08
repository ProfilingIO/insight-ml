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

import java.util.Map.Entry;

import org.apache.commons.math3.util.Pair;

import com.google.common.base.Objects;
import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.AbstractModel;
import com.insightml.models.IModel;
import com.insightml.utils.Arrays;
import com.insightml.utils.types.collections.PairList;
import com.insightml.utils.ui.UiUtils;

public final class BoostingModel extends AbstractModel<ISample, Double> {

	private static final long serialVersionUID = -8115269534209318613L;

	private IModel<ISample, Double> first;
	private PairList<IModel<ISample, Double>, Double> steps;

	BoostingModel() {
	}

	public BoostingModel(final IModel<ISample, Double> first, final PairList<IModel<ISample, Double>, Double> steps) {
		super(null);
		this.first = first;
		this.steps = steps;
	}

	@Override
	public Double[] apply(final ISamples<ISample, ?> instances) {
		double[] preds = Arrays.cast(first.apply(instances));
		for (final Pair<IModel<ISample, Double>, Double> step : steps) {
			final double[] fit = Arrays.cast(step.getFirst().apply(instances));
			preds = GBM.updatePredictions(preds, fit, step.getSecond());
		}
		return Arrays.cast(preds);
	}

	@Override
	public SumMap<String> featureImportance() {
		final SumMapBuilder<String> builder = SumMap.builder(false);
		for (final Pair<IModel<ISample, Double>, Double> step : steps) {
			for (final Entry<String, Double> imp : step.getFirst().featureImportance()) {
				builder.increment(imp.getKey(), imp.getValue());
			}
		}
		return builder.build(0);
	}

	@Override
	public String info() {
		return steps.size() > 1 ? steps.get(1).getFirst().info() + steps.getLast().getFirst().info() + "\n"
				+ UiUtils.format(featureImportance().distribution(), 0) : "No training data";
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