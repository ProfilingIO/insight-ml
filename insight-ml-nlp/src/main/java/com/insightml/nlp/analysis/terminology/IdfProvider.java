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
package com.insightml.nlp.analysis.terminology;

import java.util.Map.Entry;

import com.insightml.math.types.ISumMap;
import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.nlp.ISentence;
import com.insightml.nlp.ITextProvider;
import com.insightml.nlp.IToken;
import com.insightml.nlp.LanguagePipeline;
import com.insightml.nlp.transformation.ISegmenter;
import com.insightml.utils.Check;
import com.insightml.utils.jobs.SimpleThreaded;

public class IdfProvider extends AbstractTermVectorProvider<Double> {
	private static final long serialVersionUID = 8701733103940968194L;

	private final IntSumMap<IToken> df;
	private final int minDF;
	private final int documents;

	public IdfProvider(final Iterable<? extends ITextProvider> referenceCorpus, final LanguagePipeline pipeline,
			final int minDF, final ISegmenter segmenter) {
		this.minDF = Check.num(minDF, 1, 10);
		final IntSumMapBuilder<IToken> corpus = IntSumMap.builder(true, 16);
		final TermFrequencyProvider tf = new TermFrequencyProvider();
		documents = new SimpleThreaded<ITextProvider>() {
			@Override
			protected void run(final int i, final ITextProvider instance) {
				if (instance.getText() != null) {
					for (final Entry<IToken, ? extends Number> token : tf
							.run(pipeline.run(segmenter.run(instance, false)), 0)) {
						corpus.increment(token.getKey(), 1);
					}
				}
			}
		}.run(referenceCorpus);
		df = Check.notNull(corpus.build(minDF + 1));
	}

	public IdfProvider(final IntSumMap<IToken> df, final int minDF, final int documents) {
		this.df = df;
		this.minDF = Check.num(minDF, 0, 10);
		this.documents = documents;
	}

	@Override
	public String getName() {
		return "Idf";
	}

	@Override
	public String getDescription() {
		return "log(N/max(" + minDF + ",df)) based on df statistics for " + documents + " documents.";
	}

	@Override
	public ISumMap<IToken, Double> tokens(final Iterable<IToken> tokens, final double min) {
		final SumMapBuilder<IToken> tfidf = new SumMapBuilder<>(true, false);
		final BinaryTermVectorProvider tf = new BinaryTermVectorProvider();
		for (final Entry<IToken, ? extends Number> entry : tf.tokens(tokens, 0)) {
			tfidf.put(entry.getKey(), getInverseDocumentFrequency(entry.getKey()));
		}
		return tfidf.build(min);
	}

	public ISumMap<IToken, Double> tokens(final Iterable<? extends ISentence> sentences, final int minTokenDF) {
		Check.num(minTokenDF, minDF, 99);
		final SumMapBuilder<IToken> tfidf = new SumMapBuilder<>(true, false);
		final BinaryTermVectorProvider tf = new BinaryTermVectorProvider();
		for (final Entry<IToken, ? extends Number> entry : tf.run(sentences)) {
			final int dFreq = getDocumentFrequency(entry.getKey());
			if (dFreq >= minTokenDF) {
				tfidf.put(entry.getKey(), inverseDocumentFrequency(dFreq));
			}
		}
		return tfidf.build(0);
	}

	public final double getInverseDocumentFrequency(final IToken token) {
		return inverseDocumentFrequency(df.get(token, minDF));
	}

	private double inverseDocumentFrequency(final int dFreq) {
		return Check.num(Math.log((documents + 1.0) / dFreq), 0.000000001, 999999);
	}

	public final int getDocumentFrequency(final IToken token) {
		return df.get(token, 0);
	}

	public final IntSumMap<IToken> getDocumentFrequencies() {
		return df;
	}

}
