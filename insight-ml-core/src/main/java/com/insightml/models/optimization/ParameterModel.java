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
package com.insightml.models.optimization;

import com.insightml.data.samples.Sample;
import com.insightml.data.samples.ISamples;
import com.insightml.models.AbstractModel;
import com.insightml.models.IModel;

public final class ParameterModel<I extends Sample, T, C> extends AbstractModel<I, Double> {

	private static final long serialVersionUID = 5493923325394278892L;

	private final T train;
	private final int labelIndex;
	double[] params;
	private final AbstractParameterLearner<I, ?, T, C> learner;

	public ParameterModel(final T train, final double[] params, final int labelIndex,
			final AbstractParameterLearner<I, ?, T, C> learner) {
		super(null);
		this.train = train;
		this.params = params.clone();
		this.labelIndex = labelIndex;
		this.learner = learner;
	}

	@Override
	public Double[] apply(final ISamples<I, ?> instances) {
		final C[] cachable = learner.computeCachable(instances, train);
		return run(instances, cachable);
	}

	Double[] run(final ISamples<I, ?> instances, final C[] cachable) {
		final Double[] preds = new Double[instances.size()];
		for (int i = 0; i < preds.length; ++i) {
			preds[i] = learner.predict(params, cachable[i], labelIndex);
		}
		return preds;
	}

	@Override
	public void close() {
		if (train instanceof AbstractModel) {
			((AbstractModel<I, Double>) train).close();
		} else if (train instanceof IModel[]) {
			for (final IModel<I, Double> model : (IModel[]) train) {
				model.close();
			}
		}
	}

	@Override
	public String getName() {
		return train == null ? "ParameterModel" : train.toString();
	}

}