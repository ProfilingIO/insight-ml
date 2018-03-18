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
package com.insightml.data.features;

import java.util.HashMap;
import java.util.Map;

import com.insightml.utils.Check;

public class FeaturesMapBuilder implements FeaturesConsumer {
	private final Map<String, Double> features = new HashMap<>();

	@Override
	public void add(final String feature, final double value) {
		Check.isNull(features.put(feature, value), feature);
	}

	public Double get(final CharSequence feature) {
		return features.get(feature);
	}

	public Map<String, Double> get() {
		return features;
	}

	public int size() {
		return features.size();
	}

}
