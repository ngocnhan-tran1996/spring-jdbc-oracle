package io.ngocnhan_tran1996.spring.jdbc.oracle.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringsTest {

    @Test
    void testIsBlank() {

        assertTrue(Strings.isBlank(null));
        assertTrue(Strings.isBlank(""));
        assertTrue(Strings.isBlank("        "));

        assertFalse(Strings.isBlank("  Test "));
    }

    @Test
    void testIsNotBlank() {

        assertFalse(Strings.isNotBlank(null));
        assertFalse(Strings.isNotBlank(""));
        assertFalse(Strings.isNotBlank("        "));

        assertTrue(Strings.isNotBlank("  Test "));
    }

    @Test
    void testFirstNoneBlank() {

        assertNull(Strings.firstNoneBlank());
        assertNull(Strings.firstNoneBlank((String) null));
        assertNull(Strings.firstNoneBlank(null, "", null));
        assertNull(Strings.firstNoneBlank(null, null, " "));

        assertEquals("X", Strings.firstNoneBlank(null, "X", null));
        assertEquals("X", Strings.firstNoneBlank(null, "X", " "));
        assertEquals("X", Strings.firstNoneBlank(null, "", "X"));
    }

    @Test
    void testEqualsIgnoreCase() {

        assertFalse(Strings.equalsIgnoreCase(null, null));
        assertFalse(Strings.equalsIgnoreCase(null, "X"));
        assertFalse(Strings.equalsIgnoreCase("X", null));

        assertTrue(Strings.equalsIgnoreCase("X", "x"));
    }

}