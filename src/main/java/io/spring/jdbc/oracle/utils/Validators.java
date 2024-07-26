package io.spring.jdbc.oracle.utils;

import io.spring.jdbc.oracle.exception.ValueException;

public final class Validators {

    private static final String NOT_BLANK = "%s must not be blank";
    private static final String NOT_NULL = "%s must not be null";

    private Validators() {
    }

    public static <T> T requireNotNull(final T value, final String message) {

        if (value == null) {

            throw new ValueException(NOT_NULL.formatted(message));
        }

        return value;
    }

    public static String requireNotBank(final String value, final String message) {

        if (Strings.isBlank(value)) {

            throw new ValueException(NOT_BLANK.formatted(message));
        }

        return value;
    }

}