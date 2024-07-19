package io.ngocnhan_tran1996.spring.jdbc.oracle.utils;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public final class Strings {

    public static final String EMPTY = "";
    public static final String NOT_BLANK = "%s must not be blank";
    public static final String NOT_NULL = "%s must not be null";

    private Strings() {
    }

    public static boolean isBlank(final String input) {

        return input == null
            || input.isBlank();
    }

    public static boolean isNotBlank(final String input) {

        return not(isBlank(input));
    }

    public static String firstNoneBlank(final String... values) {

        return stream(values)
            .filter(Strings::isNotBlank)
            .findFirst()
            .orElse(null);
    }

    public static boolean equalsIgnoreCase(final String value, final String compareValue) {

        return value != null && value.equalsIgnoreCase(compareValue);
    }

    private static Stream<String> stream(final String... values) {

        return Optional.ofNullable(values)
            .stream()
            .flatMap(Arrays::stream);
    }

}