package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import io.spring.jdbc.oracle.utils.Mappers;
import io.spring.jdbc.oracle.utils.Validators;
import java.lang.reflect.Array;
import java.util.Collection;
import org.springframework.core.convert.TypeDescriptor;

final class CollectionToArrayGenericOracleConverter implements GenericOracleConverter {

    private final OracleConverters oracleConverters;
    private TypeDescriptor sourceType;
    private TypeDescriptor targetType;

    CollectionToArrayGenericOracleConverter(OracleConverters oracleConverters) {

        this.oracleConverters = oracleConverters;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        this.sourceType = sourceType;
        this.targetType = targetType;

        var target = Mappers.resolveArrayTypeDescriptor(targetType).getType();

        return Collection.class.isAssignableFrom(sourceType.getType())
            && Object[].class.isAssignableFrom(target);
    }

    @Override
    public ConvertKey getConvertKey() {

        return new ConvertKey(Collection.class, Object[].class);
    }

    @Override
    public Object convert(Object source) {

        if (source == null) {

            return null;
        }

        var sourceCollection = (Collection<?>) source;
        var targetElementType = Validators.requireNotNull(
            targetType.getElementTypeDescriptor(),
            "targetElementType"
        );
        var array = Array.newInstance(targetElementType.getType(), sourceCollection.size());

        int i = 0;
        for (var sourceElement : sourceCollection) {

            var targetElement = this.oracleConverters.convert(
                sourceElement,
                sourceType.elementTypeDescriptor(sourceElement),
                targetElementType
            );

            Array.set(array, i++, targetElement);
        }

        return array;
    }

}