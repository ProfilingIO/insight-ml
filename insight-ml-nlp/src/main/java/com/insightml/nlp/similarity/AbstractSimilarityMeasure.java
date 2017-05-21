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
package com.insightml.nlp.similarity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.Sample;
import com.insightml.evaluation.functions.AbstractObjectiveFunctionFrame;
import com.insightml.math.statistics.Stats;
import com.insightml.nlp.ISentence;
import com.insightml.nlp.ITermVectorProvider;
import com.insightml.nlp.IToken;
import com.insightml.nlp.LanguagePipeline;
import com.insightml.utils.Check;
import com.insightml.utils.Collections;
import com.insightml.utils.jobs.Threaded;

public abstract class AbstractSimilarityMeasure<K>
		extends AbstractObjectiveFunctionFrame<ISentence[], Iterable<? extends ISentence>>
		implements ITerminologySimilarity<K> {
	private static final long serialVersionUID = 3089787979916847894L;

	private final ITermVectorProvider<Number> terminology;

	protected AbstractSimilarityMeasure(final ITermVectorProvider<? extends Number> terminology) {
		this.terminology = (ITermVectorProvider<Number>) terminology;
	}

	public final double similarity(final ISentence text1, final ISentence text2) {
		return similarity((Map<K, Number>) terminology.tokens(text1, 0).getMap(),
				(Map<K, Number>) terminology.tokens(text2, 0).getMap());
	}

	@Override
	public final double similarity(final Iterable<? extends ISentence> text1,
			final Iterable<? extends ISentence> text2) {
		return similarity(text1, terminology.run(text2, 0).getMap());
	}

	public final double similarity(final Iterable<? extends ISentence> text1,
			final Map<IToken, ? extends Number> map2) {
		final Map<IToken, ? extends Number> map1 = terminology.run(text1, 0).getMap();
		if (map1.isEmpty()) {
			return map2.isEmpty() ? 1 : 0;
		}
		if (map2.isEmpty()) {
			return 0;
		}
		return similarity((Map<K, Number>) map1, (Map<K, Number>) map2);
	}

	public final Stats statsSentences(final ISentence sentence, final Iterable<? extends ISentence> sentences) {
		final Stats stats = new Stats();
		final Map<IToken, ? extends Number> tokens = terminology.tokens(sentence, 0).getMap();
		for (final ISentence sent : sentences) {
			final Map<IToken, ? extends Number> tokens2 = terminology.tokens(sent, 0).getMap();
			stats.add(similarity((Map<K, Number>) tokens, (Map<K, Number>) tokens2));
		}
		return stats;
	}

	public final Stats statsTexts(final Iterable<ISentence> text, final Iterable<? extends Iterable<ISentence>> texts,
			final LanguagePipeline pipeline, final double minTF) {
		final Map<IToken, ? extends Number> tfText = terminology.run(pipeline.run(text), minTF).getMap();
		final Collection<Map<K, ? extends Number>> txts = Collections
				.values(new Threaded<Iterable<ISentence>, Map<K, ? extends Number>>() {
					@Override
					protected Map<K, ? extends Number> exec(final int i, final Iterable<ISentence> txt) {
						return getTermVectorProvider().run(pipeline.run(txt), minTF).getMap();
					}
				}.run(texts, 1));
		return statsTexts((Map<K, Number>) tfText, txts);
	}

	@Override
	public final Stats statsTexts(final Map<K, ? extends Number> termText,
			final Iterable<Map<K, ? extends Number>> texts) {
		Check.num(termText.size(), 1, 999999);
		final Stats stats = new Stats();
		for (final Pair<Map<K, ? extends Number>, Double> value : new Threaded<Map<K, ? extends Number>, Double>() {
			@Override
			protected Double exec(final int i, final Map<K, ? extends Number> ref) {
				return ref.isEmpty() ? 0 : similarity(termText, ref);
			}
		}.run(texts, 1)) {
			stats.add(value.getSecond());
		}
		return stats;
	}

	@Override
	public final double instance(final Iterable<? extends ISentence> prediction, final ISentence[] expected,
			final Sample sample, final int labelIndex) {
		return similarity(prediction, Arrays.asList(expected));
	}

	@Override
	public final ITermVectorProvider getTermVectorProvider() {
		return terminology;
	}

}
