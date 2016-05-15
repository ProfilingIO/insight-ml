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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import com.insightml.utils.Check;
import com.insightml.utils.io.IoUtils;

public final class Text extends AbstractTextProvider {
	private static final long serialVersionUID = 4141859093908764703L;

	private final String text;

	public Text(final String text, final Language language) {
		super(language);
		this.text = Check.length(text, 1, 9999999);
	}

	private static Text of(final InputStream text, final Language language) {
		return new Text(IoUtils.readFile(text, Charset.forName("UTF-8")), language);
	}

	public static Text fromFile(final String file, final Language lang) {
		return of(Text.class.getResourceAsStream(file), lang);
	}

	public static Queue<Text> fromFiles(final Language lang, final String... files) {
		final Queue<Text> texts = new LinkedList<>();
		for (final String file : files) {
			texts.add(of(Text.class.getResourceAsStream(file), lang));
		}
		return texts;
	}

	@Override
	public String getText() {
		return text;
	}
}
