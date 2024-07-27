package io.spring.jdbc.oracle.utils;

import static io.spring.jdbc.oracle.utils.Matchers.not;

public final class Strings {

    public static final String EMPTY = "";

    private Strings() {
    }

    public static boolean isNotBlank(final String input) {

        return not(isBlank(input));
    }

    public static boolean isBlank(final String input) {

        return input == null
            || input.isBlank();
    }

    public static boolean equalsIgnoreCase(final String value, final String compareValue) {

        return value != null && value.equalsIgnoreCase(compareValue);
    }

}