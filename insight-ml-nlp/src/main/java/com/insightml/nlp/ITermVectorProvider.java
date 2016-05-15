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

import java.io.Serializable;

import com.insightml.math.types.ISumMap;

public interface ITermVectorProvider<N extends Number> extends Serializable {

	String getName();

	String getDescription();

	ISumMap<IToken, ? extends N> run(Iterable<? extends ISentence> sentences, double min);

	ISumMap<IToken, N> runAll(Iterable<? extends Iterable<ISentence>> sentences, double min);

	ISumMap<IToken, ? extends N> tokens(Iterable<IToken> tokens, double min);

}
