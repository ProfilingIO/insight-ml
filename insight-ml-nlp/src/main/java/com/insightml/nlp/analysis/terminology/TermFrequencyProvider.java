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

import java.util.List;
import java.util.Map.Entry;

import com.insightml.math.types.ISumMap;
import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.math.types.SumMap.SumMapBuilder;
import com.insightml.nlp.IToken;
import com.insightml.nlp.Tokens;
import com.insightml.utils.Check;

public final class TermFrequencyProvider extends AbstractTermVectorProvider<Number> {
	private static final long serialVersionUID = -209859916907091660L;

	private final Normalization normalization;

	public TermFrequencyProvider() {
		this(Normalization.NONE);
	}

	public TermFrequencyProvider(final Normalization normalization) {
		this.normalization = Check.notNull(normalization);
	}

	@Override
	public String getName() {
		return "Tf" + (normalization == Normalization.NONE ? "" : "{Norm=" + normalization + "}");
	}

	@Override
	public ISumMap<IToken, ? extends Number> tokens(final Iterable<IToken> tokens, final double min) {
		final IntSumMap<IToken> hist = Tokens.getHistogram(tokens, (int) min);
		switch (normalization) {
		case NONE:
			return hist;
		case MAX:
			final SumMapBuilder<IToken> builder = new SumMapBuilder<>(true, false);
			if (hist.size() > 0) {
				final double max = hist.statistics().getMax();
				for (final Entry<IToken, Integer> entry : hist) {
					builder.put(entry.getKey(), entry.getValue() * 1.0 / max);
				}
			}
			return builder.build(0);
		default:
			throw new IllegalStateException(normalization.name());
		}
	}

	public static IntSumMap<IToken> run(final List<ISumMap<IToken, ? extends Number>> iterable, final int min) {
		final IntSumMapBuilder<IToken> builder = new IntSumMapBuilder<>(false, 16, false);
		for (final ISumMap<IToken, ? extends Number> inst : iterable) {
			for (final Entry<IToken, ? extends Number> token : inst) {
				builder.increment(token.getKey(), (Integer) token.getValue());
			}
		}
		return builder.build(min);
	}

	public enum Normalization {

		NONE, MAX;
	}
}
