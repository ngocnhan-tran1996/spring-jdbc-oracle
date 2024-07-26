package io.spring.jdbc.oracle.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.spring.jdbc.oracle.exception.ValueException;
import org.junit.jupiter.api.Test;

class ValidatorsTest {

    @Test
    void requireNotNull() {

        assertThrows(ValueException.class, () -> Validators.requireNotNull(null, "X"));

        assertEquals("X", Validators.requireNotNull("X", "X"));
    }

    @Test
    void requireNotBank() {

        assertThrows(ValueException.class, () -> Validators.requireNotBank(null, "X"));
        assertThrows(ValueException.class, () -> Validators.requireNotBank("", "X"));
        assertThrows(ValueException.class, () -> Validators.requireNotBank("        ", "X"));

        assertEquals("  Test ", Validators.requireNotBank("  Test ", "X"));
    }

}