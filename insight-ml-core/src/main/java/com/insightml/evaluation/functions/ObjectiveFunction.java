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
package com.insightml.evaluation.functions;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.Sample;
import com.insightml.data.samples.ISamples;
import com.insightml.models.Predictions;

public interface ObjectiveFunction<E, P> extends Serializable {

	String getName();

	String getDescription();

	DescriptiveStatistics acrossLabels(List<? extends Predictions<? extends E, ? extends P>>[] predictions);

	DescriptiveStatistics label(P[] preds, E[] expected, double[] weights, ISamples<?, ?> samples, int labelIndex);

	double instance(P pred, E expected, Sample sample);

	double normalize(double score);

}
