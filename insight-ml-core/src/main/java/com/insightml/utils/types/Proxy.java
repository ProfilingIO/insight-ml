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

import com.google.common.base.Supplier;

public abstract class Proxy<T> extends AbstractClass implements Supplier<T> {

	private final transient Object lock;
	private transient T object;

	public Proxy() {
		lock = this;
	}

	@Override
	public final T get() {
		if (object == null) {
			synchronized (lock) {
				if (object == null) {
					object = load();
				}
			}
		}
		return object;
	}

	protected abstract T load();

	public final boolean isLoaded() {
		return object != null;
	}

}
