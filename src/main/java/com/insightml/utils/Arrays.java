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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.insightml.math.Vectors;

public final class Arrays {

	private Arrays() {
	}

	public static <T> T[] of(final Iterable<? extends T> collection) {
		final List<T> list = new LinkedList<>();
		for (final T bla : collection) {
			list.add(bla);
		}
		return of(list);
	}

	public static <T> T[] of(final Iterable<? extends T> collection, final Class<T> clazz) {
		final List<T> list = new LinkedList<>();
		for (final T bla : collection) {
			list.add(bla);
		}
		return of(list, clazz);
	}

	public static <T> T[] of(final Collection<T> collection) {
		return of(collection, (Class<T>) collection.iterator().next().getClass());
	}

	public static <T> T[] of(final Collection<? extends T> collection, final Class<T> clazz) {
		if (collection == null) {
			return null;
		}
		final int size = collection.size();
		if (size == 0) {
			return null;
		}
		final T[] array = (T[]) Array.newInstance(clazz, size);
		return collection.toArray(array);
	}

	public static <T> boolean contains(final T[] array, final T element) {
		for (final T el : array) {
			if (el.equals(element)) {
				return true;
			}
		}
		return false;
	}

	public static double[] filter(final double[] orig, final boolean[] filter) {
		final double[] fil = new double[Vectors.sum(filter)];
		for (int i = 0, j = -1; i < orig.length; ++i) {
			if (filter[i]) {
				fil[++j] = orig[i];
			}
		}
		return fil;
	}

	public static float[] filter(final float[] orig, final boolean[] filter) {
		final float[] fil = new float[Vectors.sum(filter)];
		for (int i = 0, j = -1; i < orig.length; ++i) {
			if (filter[i]) {
				fil[++j] = orig[i];
			}
		}
		return fil;
	}

	public static <T> T[] filter(final T[] orig, final boolean[] filter) {
		final T[] fil = (T[]) Array.newInstance(orig[0].getClass(), Vectors.sum(filter));
		for (int i = 0, j = -1; i < orig.length; ++i) {
			if (filter[i]) {
				fil[++j] = orig[i];
			}
		}
		return fil;
	}

	@Nonnull
	public static <T> T[] fill(final int size, final T obj) {
		final T[] array = (T[]) Array.newInstance(obj.getClass(), size);
		for (int i = 0; i < array.length; ++i) {
			array[i] = obj;
		}
		return array;
	}

	public static <T> T[] fill(final int size, final Class<T> clazz) {
		final T[] array = (T[]) Array.newInstance(clazz, size);
		try {
			for (int i = 0; i < array.length; ++i) {
				array[i] = clazz.newInstance();
			}
			return array;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static double[] cast(final Double[] array) {
		final double[] arr = new double[array.length];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = array[i].doubleValue();
		}
		return arr;
	}

	@Nonnull
	public static Double[] cast(final double[] array) {
		final Double[] arr = new Double[array.length];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = array[i];
		}
		return arr;
	}

	public static double[] cast(final Collection<Double> array) {
		final double[] arr = new double[Check.num(array.size(), 1, 999999)];
		int i = -1;
		for (final Double el : array) {
			arr[++i] = el.doubleValue();
		}
		return arr;
	}

	@Nonnull
	public static double[] asDouble(final float[] array) {
		final double[] arr = new double[array.length];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = array[i];
		}
		return arr;
	}

	public static <T> List<T> merge(final List<? extends T[]> run) {
		final List<T> list = new LinkedList<>();
		for (final T[] l : run) {
			for (final T el : l) {
				list.add(el);
			}
		}
		return list;
	}

	public static <T> T[] concat(final T[] a, final T[] b) {
		return concat(a, b, (Class<T>) a[0].getClass());
	}

	public static <T> T[] concat(final T[] a, final T[] b, final Class<T> clazz) {
		final int aLen = a.length;
		final int bLen = b.length;
		final T[] c = (T[]) Array.newInstance(clazz, aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	public static <T> T[] replace(final T[] arr, final T old, final T repl) {
		final T[] clone = arr.clone();
		for (int i = 0; i < clone.length; ++i) {
			if (clone[i].equals(old)) {
				clone[i] = repl;
			}
		}
		return clone;
	}

	public static boolean isSubset(final int[] s, final int[] l) {
		loop: for (final int element : s) {
			for (final int element2 : l) {
				if (element == element2) {
					continue loop;
				}
			}
			return false;
		}
		return true;
	}

}
