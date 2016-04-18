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

public final class LogLoss extends AbstractObjectiveFunctionFrame<Object, Object> {

	private static final long serialVersionUID = 6038479385452595673L;

	private final boolean useBase10;

	public LogLoss(final boolean useBase10) {
		this.useBase10 = useBase10;
	}

	@Override
	public double instance(final Object prediction, final Object label, final ISample sample) {
		final boolean act = label instanceof Boolean ? (Boolean) label : ((Number) label).doubleValue() == 1;
		final double capped = Math.min(0.99999999, Math.max(0.00000001, (Double) prediction));
		final double score = act ? capped : 1 - capped;
		return useBase10 ? Math.log10(score) : Math.log(score);
	}

	@Override
	protected double getResult(final double sum, final double weightSum) {
		return -(sum / weightSum);
	}

	@Override
	public double normalize(final double score) {
		return -score;
	}

	@Override
	public String getName() {
		return useBase10 ? "Binomial Deviance" : "LogLoss";
	}

	@Override
	public String getDescription() {
		return "See https://www.kaggle.com/wiki/LogarithmicLoss";
	}

}
