package io.spring.jdbc.oracle.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringsTest {

    @Test
    void isBlank() {

        assertTrue(Strings.isBlank(null));
        assertTrue(Strings.isBlank(""));
        assertTrue(Strings.isBlank("        "));

        assertFalse(Strings.isBlank("  Test "));
    }

    @Test
    void isNotBlank() {

        assertFalse(Strings.isNotBlank(null));
        assertFalse(Strings.isNotBlank(""));
        assertFalse(Strings.isNotBlank("        "));

        assertTrue(Strings.isNotBlank("  Test "));
    }

    @Test
    void firstNoneBlank() {

        assertNull(Strings.firstNoneBlank());
        assertNull(Strings.firstNoneBlank((String) null));
        assertNull(Strings.firstNoneBlank(null, "", null));
        assertNull(Strings.firstNoneBlank(null, null, " "));

        assertEquals("X", Strings.firstNoneBlank(null, "X", null));
        assertEquals("X", Strings.firstNoneBlank(null, "X", " "));
        assertEquals("X", Strings.firstNoneBlank(null, "", "X"));
    }

    @Test
    void equalsIgnoreCase() {

        assertFalse(Strings.equalsIgnoreCase(null, null));
        assertFalse(Strings.equalsIgnoreCase(null, "X"));
        assertFalse(Strings.equalsIgnoreCase("X", null));

        assertTrue(Strings.equalsIgnoreCase("X", "x"));
        assertTrue(Strings.equalsIgnoreCase("X", "X"));
    }

}