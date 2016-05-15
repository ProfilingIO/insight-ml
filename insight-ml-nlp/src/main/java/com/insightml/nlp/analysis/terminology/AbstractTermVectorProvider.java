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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.insightml.math.types.ISumMap;
import com.insightml.nlp.ISentence;
import com.insightml.nlp.ITermVectorProvider;
import com.insightml.nlp.IToken;
import com.insightml.nlp.analysis.LanguageAnalysisProducer;
import com.insightml.utils.Collections;
import com.insightml.utils.jobs.Threaded;

abstract class AbstractTermVectorProvider<N extends Number> extends LanguageAnalysisProducer<IToken>
		implements ITermVectorProvider<N> {

	private static final long serialVersionUID = 5697222789482804878L;

	@Override
	public final ISumMap<IToken, N> runAll(final Iterable<? extends Iterable<ISentence>> sentences, final double min) {
		final Queue<IToken> tokenz = Collections
				.merge(Collections.values(new Threaded<Iterable<ISentence>, List<IToken>>() {

					@Override
					protected List<IToken> exec(final int i, final Iterable<ISentence> input) {
						final List<IToken> tokens = new LinkedList<>();
						for (final ISentence sentence : input) {
							for (final IToken token : sentence) {
								tokens.add(token);
							}
						}
						return tokens;
					}
				}.run(sentences, 1)));
		return (ISumMap<IToken, N>) tokens(tokenz, min);
	}

	@Override
	public ISumMap<IToken, N> run(final Iterable<? extends ISentence> input) {
		return (ISumMap<IToken, N>) run(input, 0);
	}

	@Override
	public final ISumMap<IToken, ? extends N> run(final Iterable<? extends ISentence> sentences, final double min) {
		final List<IToken> tokens = new LinkedList<>();
		for (final ISentence sent : sentences) {
			for (final IToken token : sent) {
				tokens.add(token);
			}
		}
		return tokens(tokens, min);
	}

}
