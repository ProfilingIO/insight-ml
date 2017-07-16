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
