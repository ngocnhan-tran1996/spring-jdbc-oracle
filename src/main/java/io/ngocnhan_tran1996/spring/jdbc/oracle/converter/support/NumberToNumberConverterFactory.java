package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverterFactory;
import org.springframework.util.NumberUtils;

class NumberToNumberConverterFactory implements OracleConverterFactory<Number, Number> {

    @Override
    public boolean matches(Class<?> sourceType, Class<?> targetType) {

        return sourceType != null
            && targetType != null
            && Number.class.isAssignableFrom(sourceType)
            && Number.class.isAssignableFrom(targetType)
            && not(sourceType.equals(targetType));
    }

    @Override
    public <T extends Number> OracleConverter<Number, T> getOracleConverter(Class<T> targetType) {

        return new NumberToNumber<>(targetType);
    }

    record NumberToNumber<T extends Number>(Class<T> targetType) implements
        OracleConverter<Number, T> {

        @Override
        public T convert(Number source) {

            return NumberUtils.convertNumberToTargetClass(source, this.targetType);
        }

    }

}