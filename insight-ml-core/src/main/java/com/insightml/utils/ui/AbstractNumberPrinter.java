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

public abstract class AbstractNumberPrinter implements INumberPrinter {

    public final String format(final double[] array) {
        final StringBuilder builder = new StringBuilder(64);
        builder.append('{');
        for (final double value : array) {
            builder.append(format(value));
            builder.append(", ");
        }
        builder.replace(builder.length() - 2, builder.length(), "}");
        return builder.toString();
    }

    public final String format(final double[][] array) {
        final StringBuilder builder = new StringBuilder(64);
        builder.append('{');
        for (final double[] array2 : array) {
            builder.append('{');
            for (final double value : array2) {
                builder.append(format(value));
                builder.append(", ");
            }
            builder.replace(builder.length() - 2, builder.length(), "}, ");
        }
        builder.replace(builder.length() - 2, builder.length(), "}");
        return builder.toString();
    }

}
