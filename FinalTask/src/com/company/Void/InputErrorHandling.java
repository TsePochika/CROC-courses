package com.company.Void;

import java.util.Objects;

public class InputErrorHandling {
    public static Object[] requireNotEmptyArray(Object[] arr, String msg) {
        if (Objects.requireNonNull(arr, msg).length == 0) {
            throw new IllegalArgumentException(msg);
        }
        return arr;
    }

    public static String requireNotEmptyString(String string, String msg) throws IllegalArgumentException {
        if (Objects.requireNonNull(string, msg).isBlank()) {
            throw new IllegalArgumentException(msg);
        }
        return string;
    }

    public static int requireUnsignedInt(int num) throws IllegalArgumentException {
        if (num < 0) {
            throw new IllegalArgumentException();
        }
        return num;
    }
    public static long requireUnsignedLong(long num) throws IllegalArgumentException {
        if (num < 0) {
            throw new IllegalArgumentException();
        }
        return num;
    }
}
