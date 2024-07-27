package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ObjectUtils;

final class ArrayToArrayGenericOracleConverter implements GenericOracleConverter {

    private final CollectionToArrayGenericOracleConverter converter;

    ArrayToArrayGenericOracleConverter(OracleConverters oracleConverters) {

        this.converter = new CollectionToArrayGenericOracleConverter(oracleConverters);
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        this.converter.matches(sourceType, targetType);

        return Object[].class.isAssignableFrom(sourceType.getType())
            && Object[].class.isAssignableFrom(targetType.getType());
    }

    @Override
    public ConvertKey getConvertKey() {

        return new ConvertKey(Object[].class, Object[].class);
    }

    @Override
    public Object convert(Object source) {

        if (source == null) {

            return null;
        }

        List<Object> sourceList = Arrays.asList(ObjectUtils.toObjectArray(source));
        return this.converter.convert(sourceList);
    }

}