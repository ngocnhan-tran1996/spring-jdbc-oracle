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

    @Test
    void with_type_name_is_blank() {

        var parameterTypeValue = ParameterInput.withParameterName("x")
            .withValues(Collections.emptyList());

        // case array
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> parameterTypeValue.withArray(""));
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> parameterTypeValue.withArray(null));

        // case struct
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> parameterTypeValue.withStruct("    "));

        // case struct array
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> parameterTypeValue.withStructArray("x", null));
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> parameterTypeValue.withStructArray("x", ""));
    }

}