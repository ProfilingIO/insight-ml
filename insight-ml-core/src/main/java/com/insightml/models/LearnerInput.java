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

import com.google.common.base.Supplier;
import com.insightml.data.FeaturesConfig;
import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Samples;
import com.insightml.utils.types.Proxy;

public final class LearnerInput<S extends ISample, E, O> {
	private final Supplier<ISamples<S, E>> train;
	public final ISamples<S, E> valid;
	public final int labelIndex;

	public final FeaturesConfig<S, O> config;

	public LearnerInput(final ISamples<S, E> train, final ISamples<S, E> valid, final int labelIndex) {
		this.train = () -> train;
		this.valid = valid;
		this.labelIndex = labelIndex;
		this.config = null;
	}

	public LearnerInput(final Iterable<S> train, final ISamples<S, E> valid, final int labelIndex,
			final FeaturesConfig<S, O> config, final PreprocessingPipeline<S, E> pipe) {
		this.train = new Proxy<ISamples<S, E>>() {
			@Override
			protected ISamples<S, E> load() {
				return pipe == null ? new Samples<>(train) : pipe.run(train, true);
			}
		};
		this.valid = valid;
		this.labelIndex = labelIndex;
		this.config = config;
	}

	public ISamples<S, E> getTrain() {
		return train.get();
	}

}
