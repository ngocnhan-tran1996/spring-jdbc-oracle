package io.spring.jdbc.oracle.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MatchersTest {

    @Test
    void when_input_is_true() {

        assertFalse(Matchers.not(true));
    }

    @Test
    void when_input_is_false() {

        assertTrue(Matchers.not(false));
    }

}