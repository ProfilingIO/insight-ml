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
package com.insightml.nlp.transformation;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.common.collect.Lists;
import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.nlp.ISentence;
import com.insightml.nlp.ISentence.Tag;
import com.insightml.nlp.IToken;
import com.insightml.nlp.LanguagePipelineElement;
import com.insightml.nlp.Sentence;
import com.insightml.nlp.Sentences;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.io.IoUtils;

public class WordFilter extends LanguagePipelineElement implements IWordFilter {
	private static final long serialVersionUID = 729315209521665550L;

	private Tag tag;
	private boolean postFilter;
	private Set<String> words;

	WordFilter() {
	}

	public WordFilter(final Tag tag, final boolean postFilter, final String... wordFiles) {
		this.tag = tag;
		this.postFilter = postFilter;
		words = new HashSet<>();
		for (final String file : Check.size(wordFiles, 0, 99)) {
			for (final String line : IoUtils.readFile(file).split("\n")) {
				final String word = prep(line);
				if (word.length() > 1) {
					words.add(word);
				}
			}
		}
	}

	public WordFilter(final Tag tag, final boolean postFilter, final Set<IToken> tokens) {
		this.tag = tag;
		this.postFilter = postFilter;
		words = new HashSet<>();
		for (final IToken token : tokens) {
			final String prepped = prep(token.toString());
			if (prepped.length() > 1) {
				words.add(prepped);
			}
		}
	}

	private static String prep(final String input) {
		return input.trim().toLowerCase(Locale.ENGLISH).replace('\'', '´');
	}

	@Override
	public boolean contains(final String word) {
		return words.contains(word);
	}

	@Override
	public final ISentence run(final ISentence sentence) {
		if (!postFilter && sentence.hasTag(tag)) {
			return sentence;
		}
		Check.state(postFilter || !sentence.hasTag(Tag.STEMMED), "sentence is already stemmed");
		final IToken[] filtered = filterOrFind(sentence, true);
		if (filtered != null) {
			return new Sentence(filtered, sentence, postFilter && sentence.hasTag(tag) ? Tag.NOTAG : tag);
		}
		return null;
	}

	public final Sentences find(final Iterable<ISentence> sentences) {
		final List<Sentence> sent = Lists.newLinkedList();
		for (final ISentence sentence : sentences) {
			final IToken[] filtered = filterOrFind(sentence, false);
			if (filtered != null) {
				sent.add(new Sentence(filtered, sentence, tag));
			}
		}
		return new Sentences(sent.toArray(new Sentence[sent.size()]));
	}

	private IToken[] filterOrFind(final ISentence sentence, final boolean filter) {
		final List<IToken> filtered = Lists.newLinkedList();
		final IToken[] tokens = sentence.getArray();
		boolean ignoreThis;
		boolean ignoreNext = false;
		for (int i = 0; i < tokens.length; ++i) {
			final IToken token = tokens[i];
			final String lc = token.toString().toLowerCase();
			ignoreThis = ignoreNext;
			boolean contains = false;
			if (i < tokens.length - 1) {
				final String next = lc + " " + tokens[i + 1].toLowerCase().toString();
				if (contains(next)) {
					contains = true;
					ignoreNext = true;
				}
			}
			if (!contains) {
				contains = contains(lc);
				ignoreNext = false;
			}
			if (!contains && !ignoreThis && filter || contains && !ignoreThis && !filter) {
				filtered.add(token);
			}
		}
		return filtered.isEmpty() ? null : Arrays.of(filtered, IToken.class);
	}

	public final IntSumMap<CharSequence> counts(final Iterable<ISentence> sentences) {
		final IntSumMapBuilder<CharSequence> counts = IntSumMap.builder(false, 16);
		for (final ISentence sentence : sentences) {
			for (final IToken token : sentence) {
				final String lc = token.toLowerCase().toString();
				if (contains(lc)) {
					counts.increment(lc, 1);
				}
			}
		}
		return counts.build(0);
	}

	public int countMatches(final ISentence sentence) {
		int count = 0;
		for (final IToken token : sentence) {
			if (contains(token.toLowerCase().toString())) {
				++count;
			}
		}
		return count;
	}

}
