package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConvertAdapter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

public final class OracleConverters {

    public static final OracleConverters INSTANCE = new OracleConverters();

    private final Map<ConverterKey, OracleConvertAdapter> converterCaches = new ConcurrentHashMap<>();

    private OracleConverters() {

        addDefaultConverters(this);
    }

    private static void addDefaultConverters(OracleConverters oracleConverters) {

        oracleConverters.addOracleConverter(new NumberToStringConverter());
    }

    public OracleConvertAdapter find(Class<?> sourceType, Class<?> targetType) {

        if (sourceType == null || targetType == null) {

            return OracleConvertAdapter.NONE;
        }

        List<Class<?>> sourceCandidates = this.getClassHierarchy(sourceType);
        List<Class<?>> targetCandidates = this.getClassHierarchy(targetType);

        for (Class<?> sourceCandidate : sourceCandidates) {
            for (Class<?> targetCandidate : targetCandidates) {

                var converter = this.converterCaches.get(
                    new ConverterKey(sourceCandidate, targetCandidate)
                );

                if (converter != null) {

                    return converter;
                }

            }
        }

        return OracleConvertAdapter.NONE;
    }

    private void addOracleConverter(OracleConverter<?, ?> converter) {

        if (converter == null) {

            throw new ValueException("Converter must not be null");
        }

        var converterKey = this.getConverterKey(converter.getClass());
        if (converterKey == null) {

            throw new ValueException("Unable determine %s".formatted(converter));
        }

        this.converterCaches.put(converterKey, new OracleConvertAdapter(converter));
    }

    private ConverterKey getConverterKey(Class<?> converterClass) {

        ResolvableType resolvableType = ResolvableType.forClass(converterClass)
            .as(OracleConverter.class);
        ResolvableType[] generics = resolvableType.getGenerics();
        if (generics.length < 2) {

            return null;
        }

        Class<?> sourceType = generics[0].resolve();
        Class<?> targetType = generics[1].resolve();
        if (sourceType == null || targetType == null) {

            return null;
        }

        return new ConverterKey(sourceType, targetType);
    }

    private List<Class<?>> getClassHierarchy(Class<?> type) {

        List<Class<?>> hierarchy = new ArrayList<>(20);
        Set<Class<?>> visited = new HashSet<>(20);
        this.addToClassHierarchy(
            0,
            ClassUtils.resolvePrimitiveIfNecessary(type),
            false,
            hierarchy,
            visited
        );
        boolean array = type.isArray();

        for (int i = 0; i < hierarchy.size(); i++) {

            Class<?> candidate = array
                ? hierarchy.get(i).componentType()
                : ClassUtils.resolvePrimitiveIfNecessary(hierarchy.get(i));

            Class<?> superclass = candidate.getSuperclass();

            if (superclass != null && superclass != Object.class && superclass != Enum.class) {

                this.addToClassHierarchy(
                    i + 1,
                    candidate.getSuperclass(),
                    array,
                    hierarchy,
                    visited
                );

            }

            this.addInterfacesToClassHierarchy(candidate, array, hierarchy, visited);
        }

        if (Enum.class.isAssignableFrom(type)) {

            this.addToClassHierarchy(hierarchy.size(), Enum.class, false, hierarchy, visited);
            this.addInterfacesToClassHierarchy(Enum.class, false, hierarchy, visited);
        }

        this.addToClassHierarchy(hierarchy.size(), Object.class, array, hierarchy, visited);
        this.addToClassHierarchy(hierarchy.size(), Object.class, false, hierarchy, visited);
        return hierarchy;
    }

    private void addInterfacesToClassHierarchy(
        Class<?> type,
        boolean asArray,
        List<Class<?>> hierarchy,
        Set<Class<?>> visited) {

        for (Class<?> implementedInterface : type.getInterfaces()) {

            this.addToClassHierarchy(
                hierarchy.size(),
                implementedInterface,
                asArray,
                hierarchy,
                visited
            );
        }
    }

    private void addToClassHierarchy(
        int index,
        Class<?> type,
        boolean asArray,
        List<Class<?>> hierarchy,
        Set<Class<?>> visited) {

        if (asArray) {

            type = type.arrayType();
        }

        if (visited.add(type)) {

            hierarchy.add(index, type);
        }
    }

    private record ConverterKey(Class<?> sourceType, Class<?> targetType) {

    }

}