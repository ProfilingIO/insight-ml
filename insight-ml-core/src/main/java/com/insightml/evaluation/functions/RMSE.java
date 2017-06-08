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

import com.google.common.base.Objects;

public final class RMSE extends MSE {
	private static final long serialVersionUID = -4611891533888902754L;

	@Override
	protected double getResult(final double sum, final double weightSum) {
		return Math.sqrt(sum / weightSum);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).toString();
	}
}
