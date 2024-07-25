package io.spring.jdbc.oracle.accessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClassRecordTest {

    @Test
    void throw_exception_when_class_is_null() {

        assertThatNullPointerException()
            .isThrownBy(() -> new ClassRecord<>(null));
    }

    @Nested
    @DisplayName("method isTypeRecord")
    class IsTypeRecord {

        @Test
        void return_false_when_class_is_not_record_type() {

            assertThat(new ClassRecord<>(Object.class).isTypeRecord())
                .isFalse();

            assertThat(new ClassRecord<>(BigDecimal.class).isTypeRecord())
                .isFalse();
        }

        @Test
        void return_true_when_class_is_record_type() {

            assertThat(new ClassRecord<>(Record.class).isTypeRecord())
                .isTrue();

            assertThat(new ClassRecord<>(RecordTest.class).isTypeRecord())
                .isTrue();
        }

        record RecordTest() {

        }

    }

}