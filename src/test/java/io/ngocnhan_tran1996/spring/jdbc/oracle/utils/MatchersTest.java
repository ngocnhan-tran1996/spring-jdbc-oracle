package io.ngocnhan_tran1996.spring.jdbc.oracle.utils;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MatchersTest {

    @Test
    void when_input_is_true() {

        assertFalse(not(true));
    }

    @Test
    void when_input_is_false() {

        assertTrue(not(false));
    }

}