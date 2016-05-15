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
package com.insightml.nlp;

import java.util.Set;

import com.google.common.collect.Sets;
import com.insightml.utils.Check;

public class Sentence extends Tokens implements ISentence {

	private static final long serialVersionUID = -591170304519427711L;

	private final Language language;
	private final String original;
	private final Set<Tag> tags = Sets.newHashSet();

	public Sentence(final IToken[] tokens, final ISentence old, final Tag addTag) {
		this(tokens, old, addTag, true);
	}

	public Sentence(final IToken[] tokens, final ISentence old, final Tag addTag, final boolean keepOld) {
		this(tokens, old.getLanguage(), keepOld ? old.getOriginal() : null);
		tags.addAll(((Sentence) old).tags);
		if (addTag != Tag.NOTAG) {
			Check.state(tags.add(Check.notNull(addTag)), addTag);
		}
	}

	public Sentence(final IToken[] tokens, final Language language, final String original) {
		super(tokens);
		this.language = Check.notNull(language);
		this.original = original == null ? null : Check.length(original, 3, 199999);
	}

	@Override
	public final Language getLanguage() {
		return language;
	}

	@Override
	public final String getOriginal() {
		return original;
	}

	@Override
	public final boolean hasTag(final Tag tag) {
		return tags.contains(tag);
	}

	@Override
	public final int compareTo(final ISentence o) {
		return asString(" ").compareTo(((Tokens) o).asString(" "));
	}

	@Override
	public String toString() {
		return getOriginal();
	}

}
