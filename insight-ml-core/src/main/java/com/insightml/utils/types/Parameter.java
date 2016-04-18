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
package com.insightml.utils.types;

public final class Parameter {

	private final String key;
	private final double value;

	public Parameter(final String key, final double value) {
		this.key = key;
		this.value = value;
	}

	public Parameter(final String key, final boolean value) {
		this.key = key;
		this.value = value ? 1 : 0;
	}

	public String getKey() {
		return key;
	}

	public double getValue() {
		return value;
	}

}
