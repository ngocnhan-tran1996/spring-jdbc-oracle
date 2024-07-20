package io.ngocnhan_tran1996.spring.jdbc.oracle.utils;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ObjectUtils;

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

}