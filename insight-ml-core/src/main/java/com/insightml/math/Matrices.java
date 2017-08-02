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
package com.insightml.math;

import java.util.Random;

public final class Matrices {

	private Matrices() {
	}

	public static double[][] fill(final int rows, final int columns, final double c) {
		final double[][] m = new double[rows][columns];
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				m[i][j] = c;
			}
		}
		return m;
	}

	public static double[][] fill(final int rows, final int columns, final Random rand, final double min,
			final double max) {
		final double[][] m = new double[rows][columns];
		final double diff = max - min;
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				m[i][j] = rand.nextDouble() * diff + min;
			}
		}
		return m;
	}

	public static double[] column(final double[][] a, final int j) {
		final double[] b = new double[a.length];
		for (int i = 0; i < b.length; ++i) {
			b[i] = a[i][j];
		}
		return b;
	}

	public static float[] column(final float[][] a, final int j) {
		final float[] b = new float[a.length];
		for (int i = 0; i < b.length; ++i) {
			b[i] = a[i][j];
		}
		return b;
	}

	public static double[][] minus(final double[][] a, final double[][] b) {
		final double[][] w = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; ++i) {
			for (int j = 0; j < a[i].length; ++j) {
				w[i][j] = a[i][j] - b[i][j];
			}
		}
		return w;
	}

	public static double[][] mult(final double[][] a, final double s) {
		final double[][] b = new double[a.length][];
		for (int i = 0; i < a.length; ++i) {
			b[i] = Vectors.scale(a[i], s);
		}
		return b;
	}

	public static double[] mult(final double[][] a, final double[] v) {
		final double[] w = new double[a.length];
		for (int i = 0; i < a.length; ++i) {
			w[i] = Vectors.dot(a[i], v);
		}
		return w;
	}

	public static double[][] transpose(final double[][] m) {
		final double[][] t = new double[m[0].length][];
		for (int i = 0; i < m[0].length; i++) {
			t[i] = column(m, i);
		}
		return t;
	}

	public static double[][] copy(final double[][] a) {
		final double[][] copy = new double[a.length][];
		for (int i = 0; i < a.length; ++i) {
			copy[i] = a[i].clone();
		}
		return copy;
	}

	public static double[][] cast(final float[][] a) {
		final double[][] copy = new double[a.length][];
		for (int i = 0; i < a.length; ++i) {
			copy[i] = new double[a[i].length];
			for (int j = 0; j < copy[i].length; ++j) {
				copy[i][j] = a[i][j];
			}
		}
		return copy;
	}

}
