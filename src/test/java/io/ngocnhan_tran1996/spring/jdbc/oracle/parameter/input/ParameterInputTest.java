package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ParameterInputTest {

    @Test
    void with_type_is_null() {

        var isTypeNull = ParameterInput.withParameterName("x")
            .withValues(Collections.emptyList());
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(isTypeNull::sqlInOutParameter);
    }

}