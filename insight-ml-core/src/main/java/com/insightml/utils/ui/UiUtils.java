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
package com.insightml.utils.ui;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import com.insightml.math.distributions.DiscreteDistribution;
import com.insightml.utils.Collections;
import com.insightml.utils.Pair;
import com.insightml.utils.types.Triple;
import com.insightml.utils.types.collections.PairList;

public final class UiUtils {

    private UiUtils() {
    }

    public static String fill(final Object object, final int length) {
        final StringBuilder builder = new StringBuilder(length);
        final String string = object == null ? "null" : object.toString();
        builder.append(string);
        for (int j = string.length(); j < length; ++j) {
            builder.append(' ');
        }
        return builder.toString();
    }

    public static String toString(final Map<?, ?> map, final boolean multiLine,
            final boolean formatValues) {
        final StringBuilder builder = new StringBuilder();
        if (!multiLine) {
            builder.append('{');
        }
        for (final Entry<?, ?> entry : map.entrySet()) {
            builder.append(fill(entry.getKey(), multiLine ? 40 : 1));
            if (!multiLine) {
                builder.append(": ");
            }
            builder.append(formatValues ? format(entry.getValue()) : entry.getValue());
            builder.append(multiLine ? '\n' : ", ");
        }
        if (builder.length() < 2) {
            return "-";
        }
        if (!multiLine) {
            builder.setLength(builder.length() - 2);
            builder.append('}');
        }
        return builder.toString();
    }

    public static String format(final PairList<?, ?> list) {
        final StringBuilder builder = new StringBuilder();
        if (list == null) {
            return null;
        }
        for (final Pair<?, ?> entry : list) {
            builder.append(fill(format(entry.getFirst()), 45) + " ");
            builder.append(format(entry.getSecond()));
            builder.append('\n');
        }
        return builder.toString();
    }

    public static String format(final double doubl) {
        return format(doubl, 5);
    }

    public static String format(final double doubl, final int precision) {
        return new SimpleFormatter(precision, true).format(doubl);
    }

    public static String format(final Object object) {
        if (object instanceof Double) {
            return format(((Double) object).doubleValue());
        } else if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        } else if (object instanceof double[][]) {
            final StringBuilder builder = new StringBuilder();
            for (final double[] line : (double[][]) object) {
                builder.append(format(line) + "\n");
            }
            return builder.toString().trim();
        } else if (object instanceof char[]) {
            return new String((char[]) object);
        } else if (object instanceof Object[]) {
            return Arrays.toString((Object[]) object);
        }

        if (object instanceof PairList) {
            return format((PairList<?, ?>) object);
        } else if (object instanceof Calendar) {
            return ((Calendar) object).getTime().toString();
        } else if (object instanceof Iterable) {
            return toString((Iterable<?>) object);
        }
        return object == null ? null : object.toString();
    }

    public static String format(final DiscreteDistribution<?> object, final double min) {
        return toString(
                Collections.getTopN(((DiscreteDistribution<?>) object).getMap(), 9999999, min),
                true, true);
    }

    public static String formatSparse(final double[] arr) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] != 0) {
                builder.append(", " + i + ": ");
                builder.append(format(arr[i]));
            }
        }
        return '[' + builder.substring(2) + ']';
    }

    private static String toString(final Iterable<?> list) {
        if (!list.iterator().hasNext()) {
            return "Empty List";
        }
        final StringBuilder builder = new StringBuilder();
        int maxFirst = 30;
        for (final Object entry : list) {
            if (entry instanceof Triple) {
                maxFirst =
                        Math.max(maxFirst, ((Triple<?, ?, ?>) entry).getFirst().toString().length());
            }
        }
        for (final Object entry : list) {
            if (entry instanceof Triple) {
                builder.append(fill(((Triple<?, ?, ?>) entry).getFirst(), maxFirst + 10));
                builder.append(fill(((Triple<?, ?, ?>) entry).getSecond(), 30));
                builder.append(((Triple<?, ?, ?>) entry).getThird());
            } else {
                builder.append(format(entry));
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public static String humanReadableByteCount(final long bytes, final boolean si) {
        final int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String join(final Object[] arr, final String separator) {
        final StringBuilder builder = new StringBuilder(32);
        for (final Object element : arr) {
            builder.append(separator);
            builder.append(element);
        }
        return builder.substring(separator.length());
    }

}
