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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.SimpleFeatureConfig;
import com.insightml.data.features.selection.ManualSelectionFilter;
import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.evaluation.simulation.SplitSimulation;
import com.insightml.evaluation.simulation.optimization.IFeatureSelection;
import com.insightml.utils.Check;
import com.insightml.utils.Pair;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.types.DoublePair;

public final class LearnerPipeline<S extends ISample, E, O> extends AbstractModule
		implements Serializable, ILearnerPipeline<S, O> {

	private static final long serialVersionUID = 8008644928002890346L;

	private final ILearner<? super S, E, O> learner;
	private final double trainRatio;
	private boolean preprocess;
	private final Logger logger = LoggerFactory.getLogger(LearnerPipeline.class);

	private IFeatureSelection<S, E, O> selection;

	public LearnerPipeline(final ILearner<? super S, E, O> learner, final boolean preprocess) {
		this(learner, 1.0, preprocess);
	}

	public LearnerPipeline(final ILearner<? super S, E, O> learner, final IFeatureSelection<S, E, O> selection) {
		this(learner, 1.0, true);
		this.selection = selection;
	}

	public LearnerPipeline(final ILearner<S, E, O> learner, final double trainRatio) {
		this(learner, trainRatio, true);
	}

	public LearnerPipeline(final ILearner<? super S, E, O> learner, final double trainRatio, final boolean preprocess) {
		this.learner = Check.notNull(learner);
		this.trainRatio = trainRatio;
		this.preprocess = preprocess;
	}

	@Override
	public String getName() {
		return learner.getName();
	}

	@Override
	public ModelPipeline<S, O> run(final Iterable<S> data, final Iterable<S> unlabled,
			final FeaturesConfig<? extends S, O> config, final int labelIndex) {
		final Pair<IModel<S, O>, PreprocessingPipeline<S, E>> modelAndPipe = modelAndPipe(data, unlabled, config,
				labelIndex);
		return new ModelPipeline<>(modelAndPipe.getFirst(), modelAndPipe.getSecond(),
				config == null ? null : config.getPostProcessor(), labelIndex);
	}

	private Pair<IModel<S, O>, PreprocessingPipeline<S, E>> modelAndPipe(final Iterable<S> data,
			final Iterable<S> unlabled, final FeaturesConfig<? extends S, O> origConfig, final int labelIndex) {
		final Pair<Iterable<S>, List<S>> split = SplitSimulation.split(data, trainRatio, null);
		final Iterable<S> train = split.getFirst();
		List<S> valid = split.getSecond();
		if (trainRatio < 1) {
			logger.info("Learning on " + ((List) train).size() + " samples, " + valid.size() + " ignored.");
		}

		ISamples<S, E> valid2 = null;
		FeaturesConfig<? extends S, O> config = null;
		PreprocessingPipeline<S, E> pipe = null;
		if (preprocess) {
			if (selection != null) {
				final DoublePair<Set<String>> result = selection.run(train, origConfig, learner);
				if (result != null) {
					config = new SimpleFeatureConfig<>(origConfig.newFeatureProvider(null, new Iterable[0]),
							new ManualSelectionFilter(result.getKey(), true), null);
				}
			}
			if (config == null) {
				config = origConfig;
			}
			final Iterable<S>[] rest = new Iterable[] { unlabled, valid };
			pipe = PreprocessingPipeline.create((FeaturesConfig<S, O>) config, train, labelIndex, rest);
			if (valid != null && false) {
				valid2 = pipe.run(valid, true);
			}
		} else {
			config = origConfig;
		}
		valid = null;

		return new Pair<>((IModel<S, O>) learner.run(new LearnerInput(train, valid2, labelIndex, config, pipe)), pipe);
	}
}
