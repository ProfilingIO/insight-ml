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

public final class Maths {

    private Maths() {
    }

    public static double pow(final double x, final int n) {
        if (n < 0) {
            return 1 / pow(x, -n);
        }
        double result = 1;
        for (int i = 0; i < n; ++i) {
            result *= x;
        }
        return result;
    }

    public static boolean sample(final double x, final Random rand) {
        return rand.nextDouble() <= x;
    }

    public static double fScore(final double precision, final double recall, final double beta) {
        if (precision == 0 || recall == 0) {
            return 0;
        }
        final double power = Math.pow(beta, 2);
        return (1 + power) * precision * recall / (precision * power + recall);
    }

}
