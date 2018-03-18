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
package com.insightml.utils;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceCloser {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceCloser.class);

	private static final Deque<AutoCloseable> resources = new LinkedList<>();
	private static boolean initialized = false;

	public static <T extends AutoCloseable> T register(final T resource) {
		if (!initialized) {
			synchronized (resources) {
				if (!initialized) {
					Runtime.getRuntime().addShutdownHook(
							new Thread(ResourceCloser::closeResources, ResourceCloser.class.getSimpleName()));
					initialized = true;
				}
			}
		}

		resources.add(resource);
		return resource;
	}

	private static void closeResources() {
		final Iterator<AutoCloseable> it = resources.descendingIterator();
		while (it.hasNext()) {
			@SuppressWarnings("resource")
			final AutoCloseable resource = it.next();
			try {
				resource.close();
			} catch (final Exception e) {
				LOG.error("Error closing resource {}: {}", resource, e, e);
			}
		}
	}
}
