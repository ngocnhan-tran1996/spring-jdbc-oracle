package io.ngocnhan_tran1996.spring.jdbc.oracle.utils;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatchersTest {

    @Test
    void testNegativeExpression() {

        assertTrue(not(false));
        assertFalse(not(true));
    }

}