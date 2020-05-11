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
package com.insightml.models.general;

import java.util.Map;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.models.AbstractIndependentModel;
import com.insightml.models.AbstractLearner;
import com.insightml.models.LearnerInput;

final class PriorLearner<E> extends AbstractLearner<Sample, Object, E> {
	private static final long serialVersionUID = -7781195905598251732L;

	final Map<Integer, E> predictions;
	final String modelName;

	PriorLearner(final String modelName, final Map<Integer, E> predictions) {
		super(null);
		this.modelName = modelName;
		this.predictions = predictions;
	}

	@Override
	public String getName() {
		return modelName;
	}

	@Override
	public PriorModel run(final LearnerInput<? extends Sample, ? extends Object> input) {
		return new PriorModel();
	}

	private final class PriorModel extends AbstractIndependentModel<Sample, E> {

		private static final long serialVersionUID = 1537965181653232470L;

		public PriorModel() {
			super(null);
		}

		@Override
		protected E predict(final int instance, final ISamples<? extends Sample, ?> instances,
				final int[] featuresFilter) {
			return predictions.get(instances.getId(instance));
		}

		@Override
		public String getName() {
			return modelName;
		}
	}

}
