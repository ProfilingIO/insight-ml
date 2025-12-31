package com.insightml.data.samples;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public final class SamplesTest {

	@Test
	public void testUnequalLabels() {
		final SimpleSample s1 = new SimpleSample(1, new Double[]{ 1.0 }, new float[]{ 0.1f }, new String[]{ "f1" });
		final SimpleSample s2 = new SimpleSample(2,
				new Double[]{ 2.0, 3.0 },
				new float[]{ 0.2f },
				new String[]{ "f1" });

		final Samples<SimpleSample, Double> samples = new Samples<>(Arrays.asList(s1, s2));

		assertEquals(2, samples.numLabels());
		assertArrayEquals(new Double[]{ 1.0, 2.0 }, samples.expected(0));
		assertArrayEquals(new Double[]{ null, 3.0 }, samples.expected(1));
	}
}
