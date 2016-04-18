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

import com.insightml.data.samples.ISample;
import com.insightml.math.Maths;

public class MSE extends AbstractObjectiveFunctionFrame<Object, Object> {

	private static final long serialVersionUID = -4611891533888902754L;

	@Override
	public final double instance(final Object prediction, final Object label, final ISample sample) {
		final double[] predAndAct = toDouble(prediction, label);
		return Maths.pow(predAndAct[1] - predAndAct[0], 2);
	}

	@Override
	public final double normalize(final double score) {
		return -score;
	}
}
