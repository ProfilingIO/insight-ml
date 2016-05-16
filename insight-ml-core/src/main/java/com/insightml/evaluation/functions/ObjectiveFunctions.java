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

public class ObjectiveFunctions {

	public static final ObjectiveFunction[] METRICS_NOMIAL = new ObjectiveFunction[] { new Accuracy(0.5), new RMSE(),
			new RMSLE(), };

	public static final ObjectiveFunction[] METRICS_BINARY = new ObjectiveFunction[] { new Accuracy(0.5), new RMSE(),
			new Gini(false), };

	public static final ObjectiveFunction[] METRICS_NUMERIC = new ObjectiveFunction[] { new RMSE(), new RMSLE(),
			new MeanAbsoluteError(-99999999, 9999999), new MedianError(), };

	private ObjectiveFunctions() {
	}

}
