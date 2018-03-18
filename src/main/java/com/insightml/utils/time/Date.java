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

import java.util.Calendar;

import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractClass;

public final class Date extends AbstractClass {

    private final long ms;

    public Date(final long ms) {
        this.ms = Check.num(ms, 900000000000l, 1500000000000l);
    }

    public Date(final java.util.Date date) {
        this(date.getTime());
    }

    public boolean before(final Date other) {
        return ms < other.ms;
    }

    public Calendar cal() {
        return Dates.getCalendar(ms);
    }

    public long getTimeInMillis() {
        return ms;
    }

    private Date addSeconds(final int seconds) {
        return new Date(ms + Check.num(seconds, -36000, 38000) * 1000);
    }

    private Date addMinutes(final int minutes) {
        return addSeconds(minutes * 60);
    }

    public Date addHours(final int hours) {
        return addMinutes(hours * 60);
    }

    public double secondsDifference(final Date other) {
        return Math.abs((other.ms - ms) * 1.0 / 1000);
    }

    public double minutesDifference(final Date other) {
        return Math.abs((other.ms - ms) * 1.0 / 1000 / 60);
    }

    public double hourDifference(final Date other) {
        return Math.abs((other.ms - ms) * 1.0 / 1000 / 3600);
    }

    public double daysDifference(final Date other) {
        return Math.abs((other.ms - ms) * 1.0 / 1000 / 86400);
    }

    @Override
    public int hashCode() {
        return (int) (ms / 1000);
    }

    @Override
    public boolean equals(final Object object) {
        return this == object || ms == ((Date) object).ms;
    }

    @Override
    public String toString() {
        return cal().getTime().toString();
    }

}
