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

import com.insightml.utils.Collections;
import com.insightml.utils.jobs.Threaded;
import com.insightml.utils.pipeline.Pipeline;

public final class LanguagePipeline extends Pipeline<ISentence> {
	private static final long serialVersionUID = 7090522298303463557L;

	public LanguagePipeline(final LanguagePipelineElement... elements) {
		super(elements);
	}

	public Iterable<Iterable<ISentence>> runTexts(final Iterable<? extends Iterable<? extends ISentence>> texts) {
		return Collections.values(new Threaded<Iterable<? extends ISentence>, Iterable<ISentence>>() {
			@Override
			protected Iterable<ISentence> exec(final int i, final Iterable<? extends ISentence> input) {
				return LanguagePipeline.this.run(input);
			}
		}.run(texts, 1));
	}

	public Iterable<ISentence> run(final Iterable<? extends ISentence> texts) {
		return Collections.values(new Threaded<ISentence, ISentence>() {
			@Override
			protected ISentence exec(final int i, final ISentence input) {
				return LanguagePipeline.this.run(input);
			}
		}.run(texts, 1));
	}

}
