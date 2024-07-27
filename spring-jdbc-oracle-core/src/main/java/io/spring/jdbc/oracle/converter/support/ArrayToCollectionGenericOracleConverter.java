package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import io.spring.jdbc.oracle.utils.Mappers;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;

final class ArrayToCollectionGenericOracleConverter implements GenericOracleConverter {

    private final OracleConverters oracleConverters;
    private TypeDescriptor sourceType;
    private TypeDescriptor targetType;

    ArrayToCollectionGenericOracleConverter(OracleConverters oracleConverters) {

        this.oracleConverters = oracleConverters;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        this.sourceType = sourceType;
        this.targetType = targetType;

        var source = Mappers.resolveArrayTypeDescriptor(sourceType).getType();

        return Object[].class.isAssignableFrom(source)
            && Collection.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public ConvertKey getConvertKey() {

        return new ConvertKey(Object[].class, Collection.class);
    }

    @Override
    public Object convert(Object source) {

        if (source == null) {

            return null;
        }

        int length = Array.getLength(source);
        var elementTypeDescriptor = this.targetType.getElementTypeDescriptor();
        var target = createCollection(
            this.targetType.getType(),
            Optional.ofNullable(elementTypeDescriptor)
                .map(TypeDescriptor::getType)
                .orElse(null),
            length);

        if (elementTypeDescriptor == null) {

            for (int i = 0; i < length; i++) {

                Object sourceElement = Array.get(source, i);
                target.add(sourceElement);
            }

            return target;
        }

        for (int i = 0; i < length; i++) {

            var sourceElement = Array.get(source, i);
            var targetElement = this.oracleConverters.convert(
                sourceElement,
                this.sourceType.elementTypeDescriptor(sourceElement),
                elementTypeDescriptor
            );

            target.add(targetElement);
        }

        return target;
    }

    Collection<Object> createCollection(
        Class<?> targetType,
        Class<?> elementType,
        int length) {

        if (targetType.isInterface() && targetType.isAssignableFrom(ArrayList.class)) {

            return new ArrayList<>(length);
        }

        return CollectionFactory.createCollection(targetType, elementType, length);
    }

}