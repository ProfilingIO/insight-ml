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
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.insightml.data.FeaturesConfig;
import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.PreprocessingPipelineSupplier;
import com.insightml.data.SimpleFeatureConfig;
import com.insightml.data.features.selection.ManualSelectionFilter;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.evaluation.simulation.SplitSimulation;
import com.insightml.evaluation.simulation.optimization.IFeatureSelection;
import com.insightml.utils.IArguments;
import com.insightml.utils.io.serialization.ISerializer;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.types.DoublePair;

public final class LearnerPipeline<S extends Sample, E, O> extends AbstractModule
		implements Serializable, ILearnerPipeline<S, O> {

	private static final long serialVersionUID = 8008644928002890346L;

	private final Logger logger = LoggerFactory.getLogger(LearnerPipeline.class);

	private final ILearner<? super S, E, O> learner;
	private final double trainRatio;
	private final boolean preprocess;
	private final ISerializer serializer;

	private IFeatureSelection<S, E, O> selection;

	public LearnerPipeline(final ILearner<? super S, E, O> learner) {
		this(learner, 1.0, true, null);
	}

	public LearnerPipeline(final ILearner<? super S, E, O> learner, final boolean preprocess) {
		this(learner, 1.0, preprocess, null);
	}

	public LearnerPipeline(final ILearner<? super S, E, O> learner, final IFeatureSelection<S, E, O> selection) {
		this(learner, 1.0, true, null);
		this.selection = selection;
	}

	public LearnerPipeline(final ILearner<S, E, O> learner, final double trainRatio) {
		this(learner, trainRatio, true, null);
	}

	public LearnerPipeline(final ILearner<? super S, E, O> learner, final double trainRatio, final boolean preprocess,
			final ISerializer serializer) {
		this.learner = Preconditions.checkNotNull(learner);
		this.trainRatio = trainRatio;
		this.preprocess = preprocess;
		this.serializer = serializer;
	}

	@Override
	public ILearner<? super S, E, O> getLearner() {
		return learner;
	}

	@Override
	public String getName() {
		return learner.getName();
	}

	@Override
	public ModelPipeline<S, O> run(final Iterable<S> data, final Iterable<S> unlabled,
			final FeaturesConfig<? extends S, O> config, final int labelIndex) {
		final Pair<Iterable<S>, PreprocessingPipeline<S>> modelAndPipe = modelAndPipe(data,
				config,
				learner.getOriginalArguments());
		final Iterable<S> samples = modelAndPipe.getFirst();
		final PreprocessingPipeline<S> pipe = modelAndPipe.getSecond();
		final File cacheFile = new File("cache/samples_" + samples.hashCode() + "_" + pipe.hashCode());
		final IModel<S, O> model = (IModel<S, O>) learner
				.run(createSamples(samples, pipe, cacheFile), null, config, labelIndex);
		return new ModelPipeline<>(model, pipe, config == null ? null : config.getPostProcessor(), labelIndex);
	}

	private ISamples<S, E> createSamples(final Iterable<S> train, final PreprocessingPipeline<S> pipe,
			final File serializationFile) {
		return new LearnerInputSource<S, E>(train, pipe, serializationFile, serializer).get();
	}

	public Pair<Iterable<S>, PreprocessingPipeline<S>> modelAndPipe(final Iterable<S> data,
			final FeaturesConfig<? extends S, O> origConfig, final @Nonnull IArguments arguments) {
		final Pair<Iterable<S>, List<S>> split = SplitSimulation.split(data, trainRatio, null);
		final Iterable<S> train = split.getFirst();
		List<S> valid = split.getSecond();
		if (trainRatio < 1) {
			logger.info("Learning on " + ((List<?>) train).size() + " samples, " + valid.size() + " ignored.");
		}

		FeaturesConfig<? extends S, O> config = null;
		PreprocessingPipeline<S> pipe = null;
		if (preprocess) {
			if (selection != null) {
				final DoublePair<Set<String>> result = selection.run(train, origConfig, learner);
				if (result != null) {
					config = new SimpleFeatureConfig<>(origConfig.newFeatureProvider(),
							new ManualSelectionFilter(result.getKey(), true), null);
				}
			}
			if (config == null) {
				config = origConfig;
			}
			pipe = new PreprocessingPipelineSupplier<>(data, (FeaturesConfig<S, O>) config, serializer, arguments)
					.get();
		} else {
			config = origConfig;
		}
		valid = null;

		return new Pair<>(train, pipe);
	}
}
