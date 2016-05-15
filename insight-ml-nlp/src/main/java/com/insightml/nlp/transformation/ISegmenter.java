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

import java.util.List;

import com.insightml.nlp.ISentence;
import com.insightml.nlp.ITextProvider;
import com.insightml.nlp.Language;
import com.insightml.nlp.Sentence;

public interface ISegmenter {

	List<ISentence> run(ITextProvider text, boolean keepString);

	Sentence asOneSentence(String text, Language language);

	List<Iterable<ISentence>> runAll(final Iterable<? extends ITextProvider> texts, final boolean keepString);

}
