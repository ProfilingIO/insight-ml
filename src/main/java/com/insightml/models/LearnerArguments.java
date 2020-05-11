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
package com.insightml.models;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.insightml.models.LearnerArguments.Argument;
import com.insightml.utils.Check;

public final class LearnerArguments implements Iterable<Argument>, Serializable {
	private static final long serialVersionUID = -658838407989377780L;

	private final LinkedHashMap<String, Argument> map = new LinkedHashMap<>();

	public LearnerArguments add(final String arg, final Double def, final double min, final double max) {
		double parameterSearchStepSize = (max - min) * 1.0 / 15;
		if (max > 1 && max % 1 == 0 && min % 1 == 0) {
			parameterSearchStepSize = Math.max(1, Math.round(parameterSearchStepSize));
		}
		return add(arg, def, min, max, parameterSearchStepSize);
	}

	public LearnerArguments add(final String arg, final Double def, final double min, final double max,
			final double parameterSearchStepSize) {
		map.put(arg, new Argument(arg, def, min, max, parameterSearchStepSize));
		return this;
	}

	public Argument get(final String arg) {
		return Check.notNull(map.get(arg), "The argument " + arg + " is not registered.");
	}

	public int size() {
		return map.size();
	}

	@Override
	public Iterator<Argument> iterator() {
		return map.values().iterator();
	}

	public static final class Argument implements Serializable {
		private static final long serialVersionUID = 7876434938453598121L;

		private final String arg;
		private final Double def;
		private final double min;
		private final double max;
		private final double parameterSearchStepSize;

		public Argument(final String arg, final Double def, final double min, final double max,
				final double parameterSearchStepSize) {
			this.arg = arg;
			this.def = def;
			this.min = min;
			this.max = Check.num(max, min, 999999999);
			this.parameterSearchStepSize = parameterSearchStepSize;
		}

		public String getName() {
			return arg;
		}

		public Double getDefault() {
			return def;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}

		public double getParameterSearchStepSize() {
			return parameterSearchStepSize;
		}

		public double validate(final double val) {
			return Check.num(val, min, max, arg);
		}
	}
}
