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

import java.text.DecimalFormat;

import javax.annotation.Nonnull;

import com.insightml.utils.Strings;

public final class SimpleFormatter extends AbstractNumberPrinter {

	private final DecimalFormat format;

	public SimpleFormatter() {
		this(5, true);
	}

	public SimpleFormatter(final int precision, final boolean removeFirstZero) {
		format = createDecimalFormatter(precision, removeFirstZero);
	}

	@Nonnull
	public static DecimalFormat createDecimalFormatter(final int precision) {
		return createDecimalFormatter(precision, false);
	}

	@Nonnull
	public static DecimalFormat createDecimalFormatter(final int precision, final boolean removeFirstZero) {
		return new DecimalFormat((removeFirstZero ? "" : "0") + "." + Strings.repeat("0", precision));
	}

	@Override
	public String format(final double number) {
		String str = format.format(number).replaceFirst("0+$", "");
		str = Strings.removeEnd(Strings.removeEnd(str, '.'), ',');
		return str.isEmpty() ? "0" : str;
	}

}
