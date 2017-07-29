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
package com.insightml.data;

import java.io.File;

import com.insightml.data.samples.Sample;
import com.insightml.utils.io.serialization.ISerializer;
import com.insightml.utils.pipeline.PipelineSource;

public class PreprocessingPipelineSupplier<S extends Sample> extends PipelineSource<PreprocessingPipeline<S>> {

	private final Iterable<S> trainingSamples;
	private final FeaturesConfig<S, ?> config;

	public PreprocessingPipelineSupplier(final Iterable<S> trainingSamples, final FeaturesConfig<S, ?> config,
			final ISerializer serializer) {
		this.trainingSamples = trainingSamples;
		this.config = config;

		if (serializer != null) {
			serializeResult(new File("cache/pipeline_" + trainingSamples.hashCode() + "_" + config.hashCode()),
					serializer);
			this.loadSerializedResultsIfAvailable(PreprocessingPipeline.class);
		}
	}

	@Override
	protected PreprocessingPipeline<S> load() {
		return PreprocessingPipeline.create(trainingSamples,
				config.newFeatureProvider(),
				config.newFeatureFilter(),
				config.getNormalization());
	}
}
