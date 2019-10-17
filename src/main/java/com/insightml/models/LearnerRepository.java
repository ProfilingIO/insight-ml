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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.insightml.models.general.ConstantBaseline;
import com.insightml.models.meta.GBRT;
import com.insightml.models.meta.RandomForest;
import com.insightml.models.regression.OLS;
import com.insightml.models.trees.RegTree;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public class LearnerRepository {

	private static final Map<String, ILearnerFactory<?, ?, ?>> factories = new HashMap<>();

	static {
		reg("const", arguments -> new ConstantBaseline<>(arguments.toDouble("c")));
		register(GBRT.class);
		register(OLS.class);
		register("rf", RandomForest.class);
		register(RegTree.class);
	}

	public static void reg(final String id, final ILearnerFactory<?, ?, ?> factory) {
		Check.isNull(factories.put(id, factory));
	}

	public static void register(final Class<? extends ILearner<?, ?, ?>> learner) {
		register(learner.getSimpleName().toLowerCase(), learner);
	}

	public static void register(final String id, final Class<? extends ILearner<?, ?, ?>> learner) {
		reg(id, arguments -> {
			Constructor<?> inst = null;
			for (final Constructor<?> con : learner.getConstructors()) {
				if (con.getParameterTypes().length == 1 || inst == null && con.getParameterTypes().length == 0) {
					inst = con;
				}
			}
			try {
				return (ILearner) (inst.getParameterTypes().length == 0 ? learner.newInstance()
						: inst.newInstance(arguments));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	public static ILearner<?, ?, ?> get(final String id, final IArguments arguments) {
		return Check.notNull(factories.get(id), id).create(arguments);
	}
}
