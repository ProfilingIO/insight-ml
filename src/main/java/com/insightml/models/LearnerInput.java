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

import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.insightml.data.FeaturesConfig;
import com.insightml.data.PreprocessingPipelineSupplier;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;

public final class LearnerInput<S extends Sample, E> {
	private ISamples<S, E> train;
	public @Nullable ISamples<S, E> valid;
	public @Nullable FeaturesConfig<S, ?> config;
	public int labelIndex;

	LearnerInput() {
	}

	public LearnerInput(final ISamples<S, E> train, final int labelIndex) {
		this(train, null, null, labelIndex);
	}

	public LearnerInput(final Supplier<ISamples<S, E>> train, final @Nullable ISamples<S, E> valid,
			final @Nullable FeaturesConfig<S, ?> config, final int labelIndex) {
		this(train.get(), valid, config, labelIndex);
	}

	public LearnerInput(final ISamples<S, E> train, final @Nullable ISamples<S, E> valid,
			final @Nullable FeaturesConfig<S, ?> config, final int labelIndex) {
		this.train = train;
		this.valid = valid;
		this.config = config;
		this.labelIndex = labelIndex;
	}

	public static <S extends Sample, E, O> LearnerInput<S, E> of(final Iterable<S> data,
			final FeaturesConfig<S, O> config) {
		return new LearnerInput<>(
				Suppliers.memoize(
						() -> new PreprocessingPipelineSupplier<>(data, config, null, null).get().run(data, true)),
				null, config, 0);
	}

	public ISamples<S, E> getTrain() {
		return train;
	}

	@Override
	public int hashCode() {
		return Objects.hash(train, valid, config, labelIndex);
	}

}
