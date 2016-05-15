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

import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.nlp.IToken;

public final class BinaryTermVectorProvider extends AbstractTermVectorProvider<Integer> {

	private static final long serialVersionUID = -6348359619620116325L;

	@Override
	public IntSumMap<IToken> tokens(final Iterable<IToken> tokens, final double min) {
		final IntSumMapBuilder<IToken> jaccard = new IntSumMapBuilder<>(false, 16, true);
		for (final IToken token : tokens) {
			jaccard.put(token.toLowerCase(), 1, true);
		}
		return jaccard.build(1);
	}
}
