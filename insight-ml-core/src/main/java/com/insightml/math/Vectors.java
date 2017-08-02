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

import java.util.Arrays;

import com.insightml.utils.Check;

public final class Vectors {

	private Vectors() {
	}

	public static double[] fill(final double c, final int length) {
		final double[] arr = new double[length];
		for (int i = 0; i < length; ++i) {
			arr[i] = c;
		}
		return arr;
	}

	public static double[] add(final double[] a, final double[] b) {
		Check.equals(a.length, b.length, null);
		final double[] c = a.clone();
		for (int i = 0; i < c.length; ++i) {
			c[i] += b[i];
		}
		return c;
	}

	public static double[] minus(final double[] a, final double[] b) {
		Check.equals(a.length, b.length, null);
		final double[] c = a.clone();
		for (int i = 0; i < c.length; ++i) {
			c[i] -= b[i];
		}
		return c;
	}

	public static int sum(final int[] counts) {
		int sum = 0;
		for (final int c : counts) {
			sum += c;
		}
		return sum;
	}

	public static double sum(final double[] scores) {
		double sum = 0;
		for (final double c : scores) {
			sum += c;
		}
		return sum;
	}

	public static int sum(final Iterable<Integer> vals) {
		int sum = 0;
		for (final int val : vals) {
			sum += val;
		}
		return sum;
	}

	public static int sum(final boolean[] values) {
		int sum = 0;
		for (final boolean c : values) {
			if (c) {
				++sum;
			}
		}
		return sum;
	}

	public static double dot(final double[] a, final double[] b) {
		Check.equals(a.length, b.length, null);
		double c = 0;
		for (int i = 0; i < a.length; ++i) {
			c += a[i] * b[i];
		}
		return c;
	}

	public static double dot(final double[] a, final boolean[] b) {
		Check.equals(a.length, b.length, null);
		double c = 0;
		for (int i = 0; i < a.length; ++i) {
			if (b[i]) {
				c += a[i];
			}
		}
		return c;
	}

	public static double[] scale(final double[] a, final double b) {
		final double[] c = a.clone();
		for (int i = 0; i < c.length; ++i) {
			c[i] *= b;
		}
		return c;
	}

	private static double[] mult(final double[] a, final double[] b) {
		Check.equals(a.length, b.length, null);
		final double[] c = a.clone();
		for (int i = 0; i < c.length; ++i) {
			c[i] *= b[i];
		}
		return c;
	}

	private static double[] pow(final double[] a, final int n) {
		final double[] c = new double[a.length];
		for (int i = 0; i < a.length; ++i) {
			c[i] = Maths.pow(a[i], n);
		}
		return c;
	}

	public static double sumPow(final double[] a, final int pow) {
		return sum(pow(a, pow));
	}

	public static double[] sqrt(final double[] a) {
		final double[] c = new double[a.length];
		for (int i = 0; i < c.length; ++i) {
			c[i] = Math.sqrt(a[i]);
		}
		return c;
	}

	public static double innerProduct(final double[] a, final double[] b) {
		return sum(mult(a, b));
	}

	public static double norm(final double[] a, final int p) {
		if (p == 2) {
			return Math.sqrt(sumPow(a, 2));
		} else if (p == 1) {
			double sum = 0;
			for (final double el : a) {
				sum += Math.abs(el);
			}
			return sum;
		}
		throw new IllegalArgumentException(p + "");
	}

	public static double euclidean(final double[] a, final double[] b) {
		return Math.sqrt(euclideanNoSqrt(a, b));
	}

	public static double euclideanNoSqrt(final double[] a, final double[] b) {
		Check.equals(a.length, b.length, "Vectors");
		double distance = 0;
		for (int i = 0; i < a.length; ++i) {
			final double diff = a[i] - b[i];
			distance += diff * diff;
		}
		return distance;
	}

	public static double euclideanNoSqrt(final float[] a, final float[] b) {
		Check.equals(a.length, b.length, "Vectors");
		double distance = 0;
		for (int i = 0; i < a.length; ++i) {
			final double diff = a[i] - b[i];
			distance += diff * diff;
		}
		return distance;
	}

	static double angle(final double x1, final double y1, final double x2, final double y2) {
		final double diff = x2 - x1;
		return diff == 0 ? 0 : Math.atan((y2 - y1) / diff);
	}

	public static int[] remove(final int[] p, final int i) {
		final int[] p2 = new int[p.length - 1];
		for (int j = 0; j < p.length; ++j) {
			if (j != i) {
				p2[j - (j < i ? 0 : 1)] = p[i];
			}
		}
		return p2;
	}

	public static int[] setSize(final int[] p, final int ks) {
		return Arrays.copyOfRange(p, 0, ks);
	}

	public static double[] unpivoting(final double[] v, final int[] index) {
		final double[] u = new double[index.length];
		for (int i = 0; i < index.length; ++i) {
			u[index[i]] = v[i];
		}
		return u;
	}

	public static double[] normalize(final double[] vec) {
		final double[] norm = new double[vec.length];
		final double sum = Vectors.sum(vec);
		for (int i = 0; i < vec.length; ++i) {
			norm[i] = vec[i] / sum;
		}
		return norm;
	}

	public static double[] append(final double[] a, final double[] v) {
		final int aLen = a.length;
		final int bLen = v.length;
		final double[] result = new double[aLen + bLen];
		System.arraycopy(a, 0, result, 0, aLen);
		System.arraycopy(v, 0, result, aLen, bLen);
		return result;
	}

	public static double[] filter(final double[] a, final boolean[] mask) {
		final double[] result = new double[sum(mask)];
		int j = -1;
		for (int i = 0; i < a.length; ++i) {
			if (mask[i]) {
				result[++j] = a[i];
			}
		}
		return result;
	}

	public static double[] cast(final float[] a) {
		final double[] out = new double[a.length];
		for (int i = 0; i < a.length; ++i) {
			out[i] = a[i];
		}
		return out;
	}

}
