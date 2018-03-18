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
package com.insightml.utils.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.insightml.utils.Check;

public final class Dates {

    private static final TimeZone timeZone = TimeZone.getTimeZone("UTC");

    static {
        TimeZone.setDefault(timeZone);
    }

    private Dates() {
    }

    public static SimpleDateFormat dateParser(final String format) {
        final SimpleDateFormat form = new SimpleDateFormat(format, Locale.ENGLISH);
        form.setTimeZone(timeZone);
        form.setLenient(false);
        return form;
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(timeZone);
    }

    public static Calendar getCalendar(final int year, final int month, final int day) {
        return getCalendar(year, month, day, 0, 0);
    }

    public static Calendar getCalendar(final int year, final int month, final int day,
            final int hour, final int minute) {
        final Calendar cal = new GregorianCalendar(timeZone);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, Check.num(month, 1, 12) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Check.num(day, 1, 31));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal;
    }

    public static Calendar getCalendar(final java.util.Date date) {
        final Calendar cal = getCalendar();
        cal.setTimeInMillis(date.getTime());
        return cal;
    }

    public static Calendar getCalendar(final long ms) {
        final Calendar date = getCalendar();
        date.setTimeInMillis(ms);
        return date;
    }

    public static int daysDifference(final Calendar date1, final Calendar date2) {
        final long timeDifference = date1.getTimeInMillis() - date2.getTimeInMillis();
        return (int) Math.abs(timeDifference * 1.0 / (86400 * 1000));
    }

    public static double hoursDifference(final Calendar date1, final Calendar date2) {
        final long timeDifference = date2.getTimeInMillis() - date1.getTimeInMillis();
        return timeDifference * 1.0 / (3600 * 1000);
    }

    public static Double minutes(final Date date1, final Date date2) {
        return minutes(date1, date2, -99999, 99999, null);
    }

    private static Double minutes(final Date date1, final Date date2, final int min, final int max,
            final Double fallback) {
        if (date1 == null || date2 == null) {
            return fallback;
        }
        final double diff = (date2.getTimeInMillis() - date1.getTimeInMillis()) * 1.0 / (60 * 1000);
        if (diff < min || diff > max) {
            throw new IllegalArgumentException(date1 + " vs. " + date2 + ": " + diff
                    + " is not within [" + min + ", " + max + "]");
        }
        return diff;
    }

    public static int getHourOfDay(final Calendar date) {
        return date.get(Calendar.HOUR_OF_DAY);
    }

    public static int getDayOfWeek(final Calendar date) {
        final int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 0 ? 7 : dayOfWeek;
    }

    public static byte getDayOfMonth(final Calendar date) {
        return (byte) date.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfYear(final Calendar date) {
        return date.get(Calendar.DAY_OF_YEAR);
    }

    public static int getWeekOfMonth(final Calendar date) {
        return date.get(Calendar.WEEK_OF_MONTH);
    }

    public static int getWeekOfYear(final Calendar date) {
        return date.get(Calendar.WEEK_OF_YEAR);
    }

    public static byte getMonth(final Calendar date) {
        return (byte) (date.get(Calendar.MONTH) + 1);
    }

    public static short getYear(final Calendar date) {
        return (short) date.get(Calendar.YEAR);
    }

    public static String toString(final Calendar date) {
        return date.getTime().toString();
    }

    public static Calendar min(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return null;
        }
        return !cal1.after(cal2) ? cal1 : cal2;
    }

    public static Calendar max(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return null;
        }
        return !cal1.before(cal2) ? cal1 : cal2;
    }
}
