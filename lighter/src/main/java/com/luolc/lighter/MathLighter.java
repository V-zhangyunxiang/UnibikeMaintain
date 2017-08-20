package com.luolc.lighter;

import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import java.util.Random;

/**
 * @author LuoLiangchen
 * @since 16/11/6
 */

public class MathLighter {

    public static int randomAround(int average) {
        double sd = average / 10;
        return randomAround(average, sd);
    }

    public static int randomAround(int average, double sd) {
        double deviation = sd * new Random().nextGaussian();
        return (int) (average - deviation);
    }

    public static int upperBoundHashCode(int upperBound, @Nullable Object... values) {
        if (upperBound <= 0) throw new IllegalArgumentException("upperBound should be a positive integer");
        int hashCode = Objects.hashCode(values);
        return Math.abs(hashCode) % upperBound;
    }
}
