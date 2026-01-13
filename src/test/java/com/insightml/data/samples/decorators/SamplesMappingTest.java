package com.insightml.data.samples.decorators;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.insightml.data.samples.Samples;
import com.insightml.data.samples.SimpleSample;

public final class SamplesMappingTest {

	@Test
	public void testMapping() {
		final SimpleSample s1 = new SimpleSample(1,
				new Double[]{ 1.0 },
				new float[]{ 0.1f, 0.5f },
				new String[]{ "f1", "f2" });
		final SimpleSample s2 = new SimpleSample(2,
				new Double[]{ 2.0 },
				new float[]{ 0.2f, 0.4f },
				new String[]{ "f1", "f2" });
		final Samples<SimpleSample, Double> samples = new Samples<>(Arrays.asList(s1, s2));
		final FeaturesDecorator<SimpleSample, Double> decorated = new FeaturesDecorator<>(samples,
				new float[][]{ { 0.1f, 0.5f }, { 0.2f, 0.4f } },
				new String[]{ "f1", "f2" });

		final int[] indexMapping = { 0, 1, 0 };
		final SamplesMapping<SimpleSample, Double> mapping = new SamplesMapping<>(decorated, indexMapping);

		assertEquals(3, mapping.size());
		assertArrayEquals(new Double[]{ 1.0, 2.0, 1.0 }, mapping.expected(0));
		assertArrayEquals(new double[]{ 1.0, 1.0, 1.0 }, mapping.weights(0), 0.0001);

		final float[][] features = mapping.features();
		assertEquals(3, features.length);
		assertArrayEquals(new float[]{ 0.1f, 0.5f }, features[0], 0.0001f);
		assertArrayEquals(new float[]{ 0.2f, 0.4f }, features[1], 0.0001f);
		assertArrayEquals(new float[]{ 0.1f, 0.5f }, features[2], 0.0001f);

		final int[][] orderedIndexes = mapping.orderedIndexes();
		assertEquals(2, orderedIndexes.length);
		// For feature 0: values are 0.1, 0.2, 0.1. Sorted: 0.1 (idx 0), 0.1 (idx 2), 0.2 (idx 1)
		// Current implementation might return [2, 1, 0] or something else.
		System.out.println("[DEBUG_LOG] orderedIndexes[0]: " + Arrays.toString(orderedIndexes[0]));
		System.out.println("[DEBUG_LOG] orderedIndexes[1]: " + Arrays.toString(orderedIndexes[1]));

		assertEquals(3, orderedIndexes[0].length);
		// One of the valid sorted orders for [0.1, 0.2, 0.1]
		final boolean match1 = java.util.Arrays.equals(new int[]{ 0, 2, 1 }, orderedIndexes[0]);
		final boolean match2 = java.util.Arrays.equals(new int[]{ 2, 0, 1 }, orderedIndexes[0]);
		org.junit.Assert.assertTrue("Ordered indexes for f0: " + java.util.Arrays.toString(orderedIndexes[0]),
				match1 || match2);
	}

	@Test
	public void testNullFirstLabel() {
		final SimpleSample s1 = new SimpleSample(1, new Double[]{ null }, new float[]{ 0.1f }, new String[]{ "f1" });
		final SimpleSample s2 = new SimpleSample(2, new Double[]{ 2.0 }, new float[]{ 0.2f }, new String[]{ "f1" });
		final Samples<SimpleSample, Double> samples = new Samples<>(java.util.Arrays.asList(s1, s2));

		final SamplesMapping<SimpleSample, Double> mapping = new SamplesMapping<>(samples, new int[]{ 0, 1 });
		assertArrayEquals(new Double[]{ null, 2.0 }, mapping.expected(0));
	}
}
