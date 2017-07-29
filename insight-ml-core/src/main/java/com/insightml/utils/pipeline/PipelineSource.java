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
package com.insightml.utils.pipeline;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.utils.io.serialization.ISerializer;

public abstract class PipelineSource<T> implements PipelineElement {

	private Class<T> serializationClass;
	private File serializationFile;
	private ISerializer serializer;
	private boolean serializeResult;
	private boolean loadSerializedResultsIfAvailable;

	@Nonnull
	public final T get() throws IOException {
		final long start = System.currentTimeMillis();
		final Logger logger = LoggerFactory.getLogger(getClass());
		if (loadSerializedResultsIfAvailable && serializationFile.exists()) {
			final T result = serializer.unserialize(new File("sub.model"), serializationClass);
			logger.info("Loaded source from {} in {} ms",
					serializationFile,
					Long.valueOf(System.currentTimeMillis() - start));
			return result;
		}
		final T result = load();
		if (serializeResult) {
			try {
				serializer.serialize(serializationFile, result);
			} catch (final Throwable e) {
				logger.error("{}", e, e);
			}
		}
		logger.info("Loaded source in {} ms", Long.valueOf(System.currentTimeMillis() - start));
		return result;
	}

	public final void consume(final PipelineConsumer<T> consumer) throws Exception {
		final long start = System.currentTimeMillis();
		consumer.consume(get());
		LoggerFactory.getLogger(getClass()).info("Consumed source in {} ms", System.currentTimeMillis() - start);
	}

	protected void loadSerializedResultsIfAvailable(final Class<?> newSerializationClass) {
		loadSerializedResultsIfAvailable = true;
		serializationClass = (Class<T>) newSerializationClass;
	}

	protected void loadSerializedResultsIfAvailable(final Class<T> newSerializationClass,
			final @Nonnull File newSerializationFile, final @Nonnull ISerializer newSerializer) {
		configureSerialization(newSerializationFile, newSerializer);
		loadSerializedResultsIfAvailable = true;
		serializationClass = newSerializationClass;
	}

	protected void serializeResult(final @Nonnull File newSerializationFile, final @Nonnull ISerializer newSerializer) {
		configureSerialization(newSerializationFile, newSerializer);
		serializeResult = true;
	}

	private void configureSerialization(final File newSerializationFile, final ISerializer newSerializer) {
		this.serializationFile = newSerializationFile;
		this.serializer = newSerializer;
	}

	@Nonnull
	protected abstract T load() throws IOException;
}
