package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import java.util.Collection;
import java.util.Optional;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;

class CollectionToCollectionGenericOracleConverter implements
    GenericOracleConverter<Collection<?>, Object> {

    private final OracleConverters oracleConverters;
    private TypeDescriptor sourceType;
    private TypeDescriptor targetType;

    CollectionToCollectionGenericOracleConverter(OracleConverters oracleConverters) {

        this.oracleConverters = oracleConverters;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        this.sourceType = sourceType;
        this.targetType = targetType;

        return Collection.class.isAssignableFrom(sourceType.getType())
            && Collection.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public Object convert(Collection<?> source) {

        if (source == null) {

            return null;
        }

        var elementTypeDescriptor = this.targetType.getElementTypeDescriptor();
        Collection<Object> target = CollectionFactory.createCollection(
            this.targetType.getType(),
            Optional.ofNullable(elementTypeDescriptor)
                .map(TypeDescriptor::getType)
                .orElse(null),
            source.size()
        );

        if (elementTypeDescriptor == null) {

            target.addAll(source);
            return target;
        }

        for (Object sourceElement : source) {

            Object targetElement = this.oracleConverters.convert(
                sourceElement,
                this.sourceType.elementTypeDescriptor(sourceElement),
                elementTypeDescriptor
            );

            target.add(targetElement);
        }

        return target;
    }

}