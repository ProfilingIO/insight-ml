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
		final int completed = counter.incrementAndGet();
		if (completed % reportEveryNTicks == 0) {
			final double done = completed * 1.0 / totalWork;
			final long timeSpent = System.currentTimeMillis() - start;
			final long timeLeft = (long) ((1 - done) / done * timeSpent);
			LOG.info("Completed {}/{} ({}%) in {} ms; {} ms left", completed, totalWork, done, timeSpent, timeLeft);
		}
	}
}
