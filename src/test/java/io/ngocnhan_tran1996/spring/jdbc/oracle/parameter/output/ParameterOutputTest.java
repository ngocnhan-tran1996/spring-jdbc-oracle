package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ParameterOutputTest {

    @Test
    void with_type_name_is_blank() {

        var isBlank = ParameterOutput.withParameterName("x")
            .withArray(" ");
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(isBlank::sqlOutParameter);

        var isNull = ParameterOutput.withParameterName("x");
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(isNull::sqlOutParameter);
    }

}