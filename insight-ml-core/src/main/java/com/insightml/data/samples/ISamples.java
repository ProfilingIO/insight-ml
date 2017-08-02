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
package com.insightml.data.samples;

import java.io.Serializable;
import java.util.Random;

import org.apache.commons.math3.util.Pair;

public interface ISamples<S extends Sample, E> extends Serializable {

	S get(int i);

	int getId(int i);

	int size();

	int numLabels();

	E[] expected(int labelIndex);

	double[] weights(int labelIndex);

	int numFeatures();

	String[] featureNames();

	float[][] features();

	int[][] orderedIndexes();

	ISamples<S, E> subset(int[] indexes);

	ISamples<S, E> subset(int from, int to);

	Pair<ISamples<S, E>, ISamples<S, E>> sample(double ratio, Random random);

	ISamples<S, E> filterFeatures(boolean[] keep);

	ISamples<S, E> sampleFeatures(double ratio, Random random);

	ISamples<S, E> randomize(Random random);

}
