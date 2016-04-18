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

import com.insightml.data.samples.ISample;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.models.AbstractModel;
import com.insightml.models.IModel;
import com.insightml.models.trees.TreeModel;

abstract class AbstractEnsembleModel<I extends ISample, E> extends AbstractModel<I, E> {

	private static final long serialVersionUID = 8994738708748217269L;

	private IModel<I, E>[] models;
	private double[] weights;

	AbstractEnsembleModel() {
	}

	AbstractEnsembleModel(final IModel<I, E>[] models, final double[] weights) {
		super(null);
		this.models = models;
		this.weights = weights;
	}

	final IModel<I, E>[] getModels() {
		return models;
	}

	final double[] getWeights() {
		return weights;
	}

	@Override
	public SumMap<String> featureImportance() {
		final SumMapBuilder<String> builder = SumMap.builder(false);
		for (int i = 0; i < models.length; ++i) {
			if (models[i] instanceof TreeModel) {
				for (final Entry<String, Double> imp : ((TreeModel) models[i]).featureImportance()) {
					builder.increment(imp.getKey(), weights[i] * imp.getValue());
				}
			}
		}
		return builder.build(0);
	}

	@Override
	public final void close() {
		for (final IModel<?, ?> model : models) {
			model.close();
		}
	}
}