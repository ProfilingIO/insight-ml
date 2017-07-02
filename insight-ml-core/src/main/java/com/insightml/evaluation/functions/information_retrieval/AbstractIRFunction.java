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
package com.insightml.evaluation.functions.information_retrieval;

import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Lists;
import com.insightml.data.samples.ISamples;
import com.insightml.evaluation.functions.AbstractObjectiveFunction;
import com.insightml.models.Predictions;
import com.insightml.utils.Check;
import com.insightml.utils.jobs.ThreadedFor;

public abstract class AbstractIRFunction<E, P> extends AbstractObjectiveFunction<E[], Collection<? extends P>> {

	private static final long serialVersionUID = 8867954567405095824L;

	private final boolean macro;

	public AbstractIRFunction(final boolean macro) {
		this.macro = macro;
	}

	@Override
	public final String getName() {
		return (macro ? "MA" : "MI") + "-" + name();
	}

	protected String name() {
		return getClass().getSimpleName();
	}

	final boolean isMacro() {
		return macro;
	}

	@Override
	public final DescriptiveStatistics acrossLabels(
			final List<? extends Predictions<? extends E[], ? extends Collection<? extends P>>>[] predictions) {
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		for (final List<? extends Predictions<? extends E[], ? extends Collection<? extends P>>> predz : Check
				.size(predictions, 1, 9999999)) {
			for (final Predictions<? extends E[], ? extends Collection<? extends P>> preds : predz) {
				for (final double val : label(preds.getPredictions(), (E[][]) preds.getExpected(), preds.getWeights(),
						preds.getSamples(), preds.getLabelIndex()).getValues()) {
					stats.addValue(val);
				}
			}
		}
		Check.num(stats.getN(), 1, 999999999);
		return stats;
	}

	@Override
	public DescriptiveStatistics label(final Collection<? extends P>[] preds, final E[][] expected,
			final double[] weights, final ISamples<?, ?> samples, final int labelIndex) {
		if (macro) {
			final DescriptiveStatistics stats = new DescriptiveStatistics();
			new ThreadedFor() {
				@Override
				protected void run(final int i) {
					stats.addValue(
							instance(preds[i], expected == null ? null : expected[i], samples.get(i), labelIndex));
				}
			}.run(0, preds.length);
			return stats;
		}
		final List<? extends E[]> list = Lists.newArrayList(expected);
		return new DescriptiveStatistics(new double[] { micro(preds, list) });
	}

	protected abstract double micro(Collection<? extends P>[] preds, List<? extends E[]> expected);

}
