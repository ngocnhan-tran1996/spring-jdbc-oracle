package io.spring.jdbc.oracle.accessor;

import static io.spring.jdbc.oracle.utils.Strings.NOT_BLANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.spring.jdbc.oracle.exception.ValueException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ParameterAccessorTest {

    @Test
    void throw_exception_when_parameterName_is_blank() {

        var msg = NOT_BLANK.formatted("parameter ");

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> new ParameterAccessorImpl<>(null, Object.class))
            .withMessage(msg);

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> new ParameterAccessorImpl<>("", Object.class))
            .withMessage(msg);

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> new ParameterAccessorImpl<>("       ", Object.class))
            .withMessage(msg);
    }

    @Test
    void throw_exception_when_class_is_null() {

        assertThatNullPointerException()
            .isThrownBy(() -> new ParameterAccessorImpl<>("X", null));
    }

    @Test
    void success_initial_when_parameterName_and_class_are_not_null() {

        // assert
        var output = new ParameterAccessorImpl<>("X", Object.class);
        assertThat(output.getParameterName())
            .isEqualTo("X");
        assertThat(output.getMappedClass())
            .isEqualTo(Object.class);
    }

    static class ParameterAccessorImpl<T> extends ParameterAccessor<T> {

        ParameterAccessorImpl(String parameterName, Class<T> mappedClass) {

            super(parameterName, mappedClass);
        }

    }

}