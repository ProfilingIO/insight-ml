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
package com.insightml.evaluation.simulation;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.FeaturesConfig;
import com.insightml.data.samples.ISample;
import com.insightml.data.utils.InstancesFilter;
import com.insightml.models.ILearnerPipeline;
import com.insightml.models.ModelPipeline;
import com.insightml.models.Predictions;
import com.insightml.utils.Check;
import com.insightml.utils.jobs.AbstractJob;
import com.insightml.utils.jobs.IJobBatch;

public final class CrossValidation<I extends ISample> extends AbstractSimulation<I> {
	private static final long serialVersionUID = 2206245632537034091L;

	private final int folds;
	private final int repetitions;

	public CrossValidation(final int folds, final int repetitions, final SimulationResultConsumer database) {
		super((repetitions == 1 ? "" : repetitions + "x ") + folds + "-Fold Cross-Validation", database);
		this.folds = Check.num(folds, 2, 20);
		this.repetitions = Check.num(repetitions, 1, 10);
	}

	@Override
	public String getDescription() {
		return (repetitions > 1 ? repetitions + " repetitions of " : "") + folds + "-fold cross-validation";
	}

	@Override
	public <E, P> SimulationResults<I, E, P>[] run(final Iterable<I> instances, final ISimulationSetup<I, E, P> setup) {
		SimulationResults<I, E, P>[] lastResult = null;
		for (final int foldss : folds == -1 ? new int[] { 2, 5, 10 } : new int[] { folds }) {
			lastResult = runCv(instances, setup);
			final ILearnerPipeline<I, P>[] learner = setup.getLearner();
			for (int i = 0; i < learner.length; ++i) {
				notify(learner[i].getName(), lastResult[i], setup, foldss * repetitions + "");
			}
		}
		return lastResult;
	}

	private <E, P> SimulationResults<I, E, P>[] runCv(final Iterable<I> instances,
			final ISimulationSetup<I, E, P> setup) {
		final ILearnerPipeline<I, P>[] learner = setup.getLearner();
		final IJobBatch<Predictions<E, P>[]> batch = ((SimulationSetup) setup).createBatch();
		final int numLabels = Check.num(instances.iterator().next().getExpected().length, 1, 10);
		final Integer labelIndex = ((SimulationSetup) setup).getLabelIndex();
		final FeaturesConfig<I, P> config = setup.getConfig();
		for (int repetition = 0; repetition < repetitions; ++repetition) {
			// repetition == 0 ? instances : instances.randomize(random);
			final Iterable<I> shuffled = instances;
			for (int fold = 1; fold <= folds; ++fold) {
				final int actualFold = fold + repetition * folds;
				if (labelIndex == null) {
					for (int index = 0; index < numLabels; ++index) {
						batch.addJob(new Fold<I, E, P>(config, shuffled, learner, fold, actualFold, folds, index));
					}
				} else {
					batch.addJob(new Fold<I, E, P>(config, shuffled, learner, fold, actualFold, folds, labelIndex));
				}
			}
		}
		final SimulationResults<I, E, P>[] result = new SimulationResults[learner.length];
		for (int i = 0; i < learner.length; ++i) {
			result[i] = new SimulationResults<>(folds * repetitions, numLabels, setup);
		}
		for (final Predictions<E, P>[] preds : batch.run()) {
			for (int i = 0; i < preds.length; ++i) {
				result[i].add(preds[i]);
			}
		}
		return result;
	}

	private static final class Fold<I extends ISample, E, P> extends AbstractJob<Predictions<E, P>[]> {
		private static final long serialVersionUID = 8592592353685668153L;

		private final int fold;
		private final int actualFold;
		private final int folds;
		private final int label;

		private final FeaturesConfig<I, P> config;
		private final Iterable<I> labled;
		private final ILearnerPipeline<I, P>[] learner;
		private final Logger logger = LoggerFactory.getLogger(Fold.class);

		Fold(final FeaturesConfig<I, P> config, final Iterable<I> shuffled, final ILearnerPipeline<I, P>[] loader,
				final int fold, final int actualFold, final int folds, final int label) {
			super("CrossValidation Fold #" + actualFold);
			this.fold = fold;
			this.actualFold = actualFold;
			this.folds = folds;
			this.label = label;

			this.config = config;
			labled = InstancesFilter.hasLabelSet(shuffled, label);
			learner = loader;
		}

		@Override
		public Predictions<E, P>[] run() {
			final Pair<List<I>, List<I>> sets = partition();
			final Predictions<E, P>[] preds = new Predictions[learner.length];
			for (int i = 0; i < preds.length; ++i) {
				final ModelPipeline<I, P> model = learner[i].run(sets.getFirst(), sets.getSecond(), config, label);
				if (false) {
					logger.info(model.info());
				}
				preds[i] = new Predictions<>(actualFold, model, sets.getSecond());
			}
			return preds;
		}

		private Pair<List<I>, List<I>> partition() {
			Check.num(fold, 1, folds);
			final List<I> train = new LinkedList<>();
			final List<I> test = new LinkedList<>();
			int i = -1;
			for (final I lab : labled) {
				final int bucket = ++i % folds;
				if (bucket == fold - 1) {
					test.add(lab);
				} else {
					train.add(lab);
				}
			}
			return new Pair<>(train, test);
		}
	}

}
