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

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProgressMonitor {
	private static final Logger LOG = LoggerFactory.getLogger(ProgressMonitor.class);

	private final int totalWork;
	private final int reportEveryNTicks;

	private final AtomicInteger counter = new AtomicInteger();
	private final long start;

	public ProgressMonitor(final int totalWork, final int reportEveryNTicks) {
		this.totalWork = totalWork;
		this.reportEveryNTicks = reportEveryNTicks;
		start = System.currentTimeMillis();
	}

	public void tick() {
		tick(null);
	}

	public void tick(final String info) {
		final int completed = counter.incrementAndGet();
		if (completed % reportEveryNTicks == 0) {
			final double done = completed * 1.0 / totalWork;
			final long timeSpent = System.currentTimeMillis() - start;
			final long timeLeft = (long) ((1 - done) / done * timeSpent);
			LOG.info("Completed {}/{} ({}%) in {} ms; {} ms left" + (info == null ? "" : "; just completed: " + info),
					completed,
					totalWork,
					done * 100,
					timeSpent,
					timeLeft);
		}
	}
}
