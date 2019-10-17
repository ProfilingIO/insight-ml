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
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.KryoException;
import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.Sample;
import com.insightml.models.general.CVLearner;
import com.insightml.models.meta.MajorityVoteLearner;
import com.insightml.utils.Arguments;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.Regex;
import com.insightml.utils.io.serialization.Serialization;
import com.insightml.utils.types.AbstractClass;

public final class ArgumentParser extends AbstractClass {
	private static final Logger LOG = LoggerFactory.getLogger(ArgumentParser.class);

	private ArgumentParser() {
	}

	public static <S extends Sample, E, O> ILearner<S, E, O>[] parseLearners(final IArguments arguments,
			final ArgumentParserContext context) {
		final List<ILearner<?, ?, ?>> learners = new LinkedList<>();
		for (int i = 1;; ++i) {
			if (!arguments.containsKey("l" + i)) {
				break;
			}
			final Arguments argSelection = new Arguments();
			for (final Entry<String, Serializable> entry : arguments.entrySet()) {
				final String key = entry.getKey();
				if (key.startsWith("l" + i + "-")) {
					argSelection.set(key.substring(3), entry.getValue());
				} else if (!key.startsWith("l") || !Regex.contains(key, Pattern.compile("^l[0-9]-"))) {
					argSelection.set(key, entry.getValue());
				}
			}
			final ILearner<?, ?, ?> learner = parseLearner(arguments.toString("l" + i), argSelection, context);
			learners.add(learner);
		}
		Check.argument(learners.size() > 0);
		return learners.toArray(new ILearner[learners.size()]);
	}

	public static ILearner<?, ?, ?> parseLearner(final String learner, final IArguments arguments,
			final ArgumentParserContext context) {
		return new LearnerProxy<>(learner, arguments, context);
	}

	private static final class LearnerProxy<S extends Sample, E, O> implements ILearner<S, E, O>, Serializable {

		private static final long serialVersionUID = 4485063208928711429L;

		private final String learner;
		private final IArguments arguments;
		private final ArgumentParserContext context;

		LearnerProxy(final String learner, final IArguments arguments, final ArgumentParserContext context) {
			this.learner = learner;
			this.arguments = arguments;
			this.context = context;
		}

		@Override
		public String getName() {
			return resolve().getName();
		}

		@Override
		public LearnerArguments arguments() {
			return resolve().arguments();
		}

		@Override
		public IArguments getOriginalArguments() {
			return arguments;
		}

		@Override
		public IModelPipeline<S, O> run(final Iterable<S> data, final Iterable<S> unlabled,
				final FeaturesConfig<? extends S, O> config, final int labelIndex,
				final ILearnerPipeline<S, O> learnerPipe) {
			final ILearner<S, E, O> resolved = resolve();
			if (context == null || !cacheModel(resolved)) {
				return resolved.run(data, unlabled, config, labelIndex, learnerPipe);
			}
			final File file = new File(context.getModelCacheDirectory() + "/model_" + resolved.getName() + "_"
					+ data.hashCode() + "_" + config.hashCode() + "_" + arguments.toString("features", "all"));
			if (file.exists()) {
				LOG.info("Loading model from {}", file);
				try {
					return Serialization.get().unserialize(file, ModelPipelineContainer.class).model;
				} catch (final Throwable e) {
					e.printStackTrace();
				}
			}
			LOG.info("Could not find model in {}, creating new", file);
			file.getParentFile().mkdirs();
			final IModelPipeline<S, O> model = resolved.run(data, unlabled, config, labelIndex, learnerPipe);
			if (file.getName().length() < 255) {
				try {
					Serialization.get().serialize(file, new ModelPipelineContainer<>(model));
				} catch (final KryoException e) {
					LOG.error("{}", e.getMessage(), e);
				}
			}
			return model;
		}

		@Override
		public IModel<S, O> run(final LearnerInput<? extends S, ? extends E> input) {
			final ILearner<S, E, O> resolved = resolve();
			if (context == null || !cacheModel(resolved)) {
				return resolved.run(input.getTrain(), input.valid, input.config, input.labelIndex);
			}
			final File file = new File(context.getModelCacheDirectory() + "/model_" + resolved.getName() + "_"
					+ input.hashCode() + "_" + arguments.toString("features", "all"));
			if (file.exists()) {
				LOG.info("Loading model from {}", file);
				try {
					return Serialization.get().unserialize(file, ModelContainer.class).model;
				} catch (final Throwable e) {
					e.printStackTrace();
				}
			}
			LOG.info("Could not find model in {}, creating new", file);
			file.getParentFile().mkdirs();
			final IModel<S, O> model = resolved.run(input.getTrain(), input.valid, input.config, input.labelIndex);
			Serialization.get().serialize(file, new ModelContainer<>(model));
			return model;
		}

		private boolean cacheModel(final ILearner<S, E, O> resolved) {
			return !(resolved instanceof MajorityVoteLearner);
		}

		private ILearner<S, E, O> resolve() {
			if (learner.endsWith(".csv")) {
				try {
					return (ILearner<S, E, O>) new CVLearner(learner, 0);
				} catch (final IOException e) {
					throw new UncheckedIOException(e);
				}
			}
			final String name = learner.toLowerCase(Locale.ENGLISH);
			return (ILearner<S, E, O>) LearnerRepository.get(name, arguments);
		}
	}

	static final class ModelContainer<S extends Sample, O> implements Serializable {
		private static final long serialVersionUID = 7889769800205553825L;

		private IModel<S, O> model;

		ModelContainer() {
		}

		public ModelContainer(final IModel<S, O> model) {
			this.model = model;
		}
	}

	static final class ModelPipelineContainer<S extends Sample, O> implements Serializable {
		private static final long serialVersionUID = -4546730350632751985L;

		private IModelPipeline<S, O> model;

		ModelPipelineContainer() {
		}

		public ModelPipelineContainer(final IModelPipeline<S, O> model) {
			this.model = model;
		}
	}
}
