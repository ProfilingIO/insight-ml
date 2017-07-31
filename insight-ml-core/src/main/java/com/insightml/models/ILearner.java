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
package com.insightml.models;

import javax.annotation.Nullable;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.utils.IArguments;

public interface ILearner<S extends Sample, E, O> {

	String getName();

	LearnerArguments arguments();

	IArguments getOriginalArguments();

	default IModelPipeline<S, O> run(final Iterable<S> data, final Iterable<S> unlabled,
			final FeaturesConfig<? extends S, O> config, final int labelIndex,
			final ILearnerPipeline<S, O> learnerPipe) {
		return learnerPipe.run(data, unlabled, config, labelIndex);
	}

	default IModel<S, O> run(final ISamples<S, E> train, final int labelIndex) {
		return run(train, null, null, labelIndex);
	}

	default IModel<S, O> run(final ISamples<? extends S, ? extends E> train,
			final @Nullable ISamples<? extends S, ? extends E> valid,
			final @Nullable FeaturesConfig<? extends S, ?> config, final int labelIndex) {
		return run(new LearnerInput<>((ISamples<S, E>) train, (ISamples<S, E>) valid, (FeaturesConfig<S, ?>) config,
				labelIndex));
	}

	IModel<S, O> run(LearnerInput<? extends S, ? extends E> input);

}
