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
package com.insightml.nlp.analysis;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.ITextSample;
import com.insightml.math.Vectors;
import com.insightml.math.types.ISumMap;
import com.insightml.math.types.SumMap;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.nlp.ISentence;
import com.insightml.nlp.LanguagePipeline;
import com.insightml.utils.jobs.SimpleThreaded;
import com.insightml.utils.pipeline.IPipelineElement;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.types.Triple;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IUiProvider;

public abstract class LanguageAnalysisProducer<R> extends AbstractModule
		implements IPipelineElement<Iterable<? extends ISentence>, ISumMap<R, ? extends Number>>,
		IUiProvider<ISamples<ITextSample, ?>> {

	private static final long serialVersionUID = 8351016024023469971L;

	public final SumMap<R> runTexts(final Queue<? extends Iterable<ISentence>> texts) {
		final SumMapBuilder<R> statements = SumMap.builder(true);
		new SimpleThreaded<Iterable<ISentence>>() {
			@Override
			protected void run(final int i, final Iterable<ISentence> sentences) {
				final ISumMap<R, ? extends Number> stats = LanguageAnalysisProducer.this.run(sentences);
				if (stats != null) {
					for (final Entry<R, ? extends Number> statement : stats) {
						statements.increment(statement.getKey(), statement.getValue().doubleValue());
					}
				}
			}
		}.run(texts);
		return statements.build(0);
	}

	public final List<Triple<R, Double, Integer>> correlation(final ISamples<? extends ITextSample, ?> instances,
			final LanguagePipeline pipeline, final int minOccurrences, final double minAbsCorrelation,
			final double minCorrelation, final double maxCorrelation, final int maxResults, final int labelIndex) {
		final List<Triple<R, Double, Integer>> tokens = bla(instances, pipeline, minOccurrences, minAbsCorrelation,
				minCorrelation, maxCorrelation, labelIndex);
		Collections.sort(tokens,
				(o1, o2) -> Double.valueOf(Math.abs(o2.getSecond())).compareTo(Math.abs(o1.getSecond())));
		return tokens.subList(0, Math.min(maxResults, tokens.size()));
	}

	private List<Triple<R, Double, Integer>> bla(final ISamples<? extends ITextSample, ?> instances,
			final LanguagePipeline pipeline, final int minOccurrences, final double minAbsCorrelation,
			final double minCorrelation, final double maxCorrelation, final int labelIndex) {
		final Pair<Map<R, boolean[]>, double[]> bla = bla(instances, pipeline, labelIndex);
		final List<Triple<R, Double, Integer>> tokens = Lists.newLinkedList();
		for (final Entry<R, boolean[]> token : bla.getFirst().entrySet()) {
			final int occurrences = Vectors.sum(token.getValue());
			if (occurrences < minOccurrences) {
				continue;
			}
			final double[] vec = new double[token.getValue().length];
			for (int i = 0; i < vec.length; ++i) {
				vec[i] = token.getValue()[i] ? 1 : 0;
			}
			final double corr = new PearsonsCorrelation().correlation(bla.getSecond(), vec);
			if (corr >= minCorrelation && corr <= maxCorrelation && Math.abs(corr) >= minAbsCorrelation) {
				tokens.add(Triple.create(token.getKey(), corr, occurrences));
			}
		}
		return tokens;
	}

	private Pair<Map<R, boolean[]>, double[]> bla(final ISamples<? extends ITextSample, ?> instances,
			final LanguagePipeline pipeline, final int labelIndex) {
		final double[] labels = new double[instances.size()];
		final Map<R, boolean[]> terms = Maps.newHashMap();
		for (int i = 0; i < labels.length; ++i) {
			labels[i] = ((Number) instances.get(i).getExpected()[labelIndex]).doubleValue();
			final ISumMap<R, ? extends Number> result = run(pipeline.run(instances.get(i)));
			if (result == null) {
				return null;
			}
			for (final Entry<R, ? extends Number> entry : result) {
				if (!terms.containsKey(entry.getKey())) {
					terms.put(entry.getKey(), new boolean[labels.length]);
				}
				terms.get(entry.getKey())[i] = true;
			}
		}
		return new Pair<>(terms, labels);
	}

	@Override
	public String getText(final ISamples<ITextSample, ?> input, final int labelIndex) {
		return UiUtils.format(correlation(input, null, 0, 0.0, -1, 1, 100, labelIndex));
	}

}
