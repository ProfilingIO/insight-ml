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

import com.insightml.utils.Check;

public class AbstractModule extends AbstractClass implements IModule {

	private String name;

	public AbstractModule() {
	}

	public AbstractModule(final String name) {
		this.name = Check.length(name, 3, 200);
	}

	@Override
	public String getName() {
		return name == null ? getClass().getSimpleName() : name;
	}

	@Override
	public String getDescription() {
		if (true) {
			return "-";
		}
		throw new IllegalAccessError(getName());
	}

}
