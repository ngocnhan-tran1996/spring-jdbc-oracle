package io.spring.jdbc.oracle.utils;

import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.property.TypeProperty;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public final class MapperUtils {

    private MapperUtils() {
    }

    public static Object[] toArrayOrNull(Object object) {

        return Optional.ofNullable(object)
            .map(o -> {

                if (o instanceof Collection<?> collection) {

                    return collection.toArray();
                }

                return ObjectUtils.toObjectArray(o);
            })
            .orElse(null);
    }

    public static Class<?> extractClass(TypeDescriptor typeDescriptor) {

        var resolvableType = Objects.requireNonNull(
                typeDescriptor,
                "STRUCT_ARRAY type is invalid"
            )
            .getResolvableType();

        resolvableType = resolvableType.isArray()
            ? resolvableType.getComponentType()
            : resolvableType.asCollection()
                .getGenerics()[0];
        return resolvableType.resolve();
    }

    public static Object convertValue(TypeProperty typeProperty, Object value) {

        var converter = typeProperty.getConverter();
        var method = BeanUtils.findMethod(
            converter,
            "convert",
            ResolvableType.forInstance(value).resolve()
        );

        if (method == null) {

            throw new ValueException("Could not found converter!");
        }

        var instance = BeanUtils.instantiateClass(converter);
        return ReflectionUtils.invokeMethod(method, instance, value);
    }

}