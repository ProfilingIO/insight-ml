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
package com.insightml.nlp.similarity;

import java.io.Serializable;
import java.util.Map;

import com.insightml.math.statistics.Stats;
import com.insightml.nlp.ISentence;
import com.insightml.nlp.ITermVectorProvider;

public interface ITerminologySimilarity<K> extends Serializable {

	String getName();

	double similarity(Iterable<? extends ISentence> vector1, Iterable<? extends ISentence> vector2);

	double similarity(Map<? extends K, ? extends Number> vector1, Map<? extends K, ? extends Number> vector2);

	Stats statsTexts(Map<K, ? extends Number> text, Iterable<Map<K, ? extends Number>> texts);

	ITermVectorProvider getTermVectorProvider();

}
