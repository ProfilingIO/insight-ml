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

import java.io.File;

import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.Samples;
import com.insightml.data.samples.decorators.FeaturesDecorator;
import com.insightml.utils.io.serialization.ISerializer;
import com.insightml.utils.pipeline.PipelineSource;

public class LearnerInputSource<S extends Sample, E> extends PipelineSource<ISamples<S, E>> {
	private final Iterable<S> train;
	private final PreprocessingPipeline<S> pipe;

	public LearnerInputSource(final Iterable<S> train, final PreprocessingPipeline<S> pipe,
			final File serializationFile, final ISerializer serializer) {
		this.train = train;
		this.pipe = pipe;

		if (pipe != null && serializationFile != null && serializer != null) {
			serializeResult(serializationFile, serializer);
			loadSerializedResultsIfAvailable(FeaturesDecorator.class);
		}
	}

	@Override
	protected ISamples<S, E> load() {
		if (pipe == null) {
			return new Samples<>(train);
		}
		final ISamples<S, E> result = pipe.run(train, true);
		result.orderedIndexes();
		return result;
	}
}
