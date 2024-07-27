package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import java.util.Collection;
import java.util.Optional;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.TypeDescriptor;

final class CollectionToCollectionGenericOracleConverter implements GenericOracleConverter {

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
    public ConvertKey getConvertKey() {

        return new ConvertKey(Collection.class, Collection.class);
    }

    @Override
    public Object convert(Object source) {

        if (source == null) {

            return null;
        }

        var sourceCollection = (Collection<?>) source;
        var elementTypeDescriptor = this.targetType.getElementTypeDescriptor();
        var target = CollectionFactory.createCollection(
            this.targetType.getType(),
            Optional.ofNullable(elementTypeDescriptor)
                .map(TypeDescriptor::getType)
                .orElse(null),
            sourceCollection.size()
        );

        if (elementTypeDescriptor == null) {

            target.addAll(sourceCollection);
            return target;
        }

        for (var sourceElement : sourceCollection) {

            var targetElement = this.oracleConverters.convert(
                sourceElement,
                this.sourceType.elementTypeDescriptor(sourceElement),
                elementTypeDescriptor
            );

            target.add(targetElement);
        }

        return target;
    }

}