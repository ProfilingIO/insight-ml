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

import java.util.Map;

import com.insightml.math.Vectors;
import com.insightml.math.vectors.IVectorSimilarity;
import com.insightml.math.vectors.VectorSimilarity;
import com.insightml.nlp.ITermVectorProvider;

public final class CosineSimilarity<K> extends AbstractSimilarityMeasure<K> implements IVectorSimilarity<K> {
	private static final long serialVersionUID = -4589545298477515191L;

	public CosineSimilarity(final ITermVectorProvider terminology) {
		super(terminology);
	}

	@Override
	public String getName() {
		return "CosSim{" + getTermVectorProvider().getName() + "}";
	}

	@Override
	public double similarity(final double[] vector1, final double[] vector2) {
		return sim(vector1, vector2);
	}

	public static double sim(final double[] vector1, final double[] vector2) {
		final double divisor = Math.sqrt(Vectors.dot(vector1, vector1)) * Math.sqrt(Vectors.dot(vector2, vector2));
		return divisor == 0 ? 0 : Vectors.dot(vector1, vector2) / divisor;
	}

	@Override
	public double similarity(final Map<? extends K, ? extends Number> vector1,
			final Map<? extends K, ? extends Number> vector2) {
		return VectorSimilarity.similarity(vector1, vector2, this);
	}

}
