package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverters;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;

public final class DefaultOracleConverters implements OracleConverters {

    public static final OracleConverters INSTANCE = new DefaultOracleConverters();

    private static final String DETERMINE_EXCEPTION = "Unable determine %s";
    private final Set<ConvertAdapter> genericConverters = new CopyOnWriteArraySet<>();
    private final Map<ConvertKey, ConvertAdapter> converterCaches = new ConcurrentHashMap<>();

    private DefaultOracleConverters() {

        addDefaultConverters(this);
    }

    @Override
    public void addGenericConverter(GenericOracleConverter<?, ?> genericOracleConverter) {

        if (genericOracleConverter == null) {

            throw new ValueException(NOT_NULL.formatted("GenericOracleConverter"));
        }

        var converterKey = getConvertKey(
            genericOracleConverter.getClass(),
            GenericOracleConverter.class
        );
        if (converterKey == null) {

            throw new ValueException(DETERMINE_EXCEPTION.formatted(genericOracleConverter));
        }

        var existConvertKey = this.genericConverters.stream()
            .anyMatch(genericConverter -> genericConverter.existConvertKey(converterKey));
        if (existConvertKey) {

            return;
        }

        this.genericConverters.add(new ConvertAdapter(genericOracleConverter, converterKey));
    }

    @Override
    public void addConverter(OracleConverter<?, ?> converter) {

        if (converter == null) {

            throw new ValueException(NOT_NULL.formatted("OracleConverter"));
        }

        var converterKey = getConvertKey(converter.getClass(), OracleConverter.class);
        if (converterKey == null) {

            throw new ValueException(DETERMINE_EXCEPTION.formatted(converter));
        }

        this.converterCaches.put(converterKey, new ConvertAdapter(converter));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        return find(sourceType, targetType)
            .getConverter()
            .convert(source);
    }

    private ConvertAdapter find(TypeDescriptor sourceType, TypeDescriptor targetType) {

        if (sourceType == null || targetType == null) {

            return ConvertAdapter.NONE;
        }

        List<Class<?>> sourceCandidates = getClassHierarchy(sourceType.getType());
        List<Class<?>> targetCandidates = getClassHierarchy(targetType.getType());

        for (var sourceCandidate : sourceCandidates) {
            for (var targetCandidate : targetCandidates) {

                var converter = this.converterCaches.get(
                    new ConvertKey(sourceCandidate, targetCandidate)
                );

                if (converter != null) {

                    return converter;
                }

            }
        }

        for (var genericConverter : this.genericConverters) {

            if (genericConverter.matches(sourceType, targetType)) {

                return genericConverter;
            }

        }

        return ConvertAdapter.NONE;
    }

    private static void addDefaultConverters(DefaultOracleConverters converters) {

        converters.addGenericConverter(new NumberToStringGenericOracleConverter());
        converters.addGenericConverter(
            new CollectionToCollectionGenericOracleConverter(converters)
        );
    }

    private static ConvertKey getConvertKey(Class<?> converterClass, Class<?> genericClass) {

        ResolvableType resolvableType = ResolvableType.forClass(converterClass)
            .as(genericClass);
        ResolvableType[] generics = resolvableType.getGenerics();
        if (generics.length < 2) {

            return null;
        }

        Class<?> sourceType = generics[0].resolve();
        Class<?> targetType = generics[1].resolve();
        if (sourceType == null || targetType == null) {

            return null;
        }

        return new ConvertKey(sourceType, targetType);
    }

    private static List<Class<?>> getClassHierarchy(Class<?> type) {

        List<Class<?>> hierarchy = new ArrayList<>(20);
        Set<Class<?>> visited = new HashSet<>(20);
        addToClassHierarchy(
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

                addToClassHierarchy(
                    i + 1,
                    candidate.getSuperclass(),
                    array,
                    hierarchy,
                    visited
                );

            }

            addInterfacesToClassHierarchy(candidate, array, hierarchy, visited);
        }

        if (Enum.class.isAssignableFrom(type)) {

            addToClassHierarchy(hierarchy.size(), Enum.class, false, hierarchy, visited);
            addInterfacesToClassHierarchy(Enum.class, false, hierarchy, visited);
        }

        addToClassHierarchy(hierarchy.size(), Object.class, array, hierarchy, visited);
        addToClassHierarchy(hierarchy.size(), Object.class, false, hierarchy, visited);
        return hierarchy;
    }

    private static void addInterfacesToClassHierarchy(
        Class<?> type,
        boolean asArray,
        List<Class<?>> hierarchy,
        Set<Class<?>> visited) {

        for (Class<?> implementedInterface : type.getInterfaces()) {

            addToClassHierarchy(
                hierarchy.size(),
                implementedInterface,
                asArray,
                hierarchy,
                visited
            );
        }
    }

    private static void addToClassHierarchy(
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

    private record ConvertKey(Class<?> sourceType, Class<?> targetType) {

    }

    private static final class ConvertAdapter {

        private static final ConvertAdapter NONE = new ConvertAdapter(new NoneConverter());

        private final GenericOracleConverter<Object, Object> converterFactory;
        private final ConvertKey converterKey;
        private final OracleConverter<Object, Object> converter;

        @SuppressWarnings("unchecked")
        ConvertAdapter(GenericOracleConverter<?, ?> converterFactory, ConvertKey converterKey) {

            this.converterFactory = (GenericOracleConverter<Object, Object>) converterFactory;
            this.converterKey = converterKey;
            this.converter = null;
        }

        @SuppressWarnings("unchecked")
        ConvertAdapter(OracleConverter<?, ?> converter) {

            this.converterFactory = null;
            this.converterKey = null;
            this.converter = (OracleConverter<Object, Object>) converter;
        }

        boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

            return this.converterFactory != null
                && this.converterFactory.matches(sourceType, targetType);
        }

        boolean existConvertKey(ConvertKey converterKey) {

            return this.converterKey != null
                && this.converterKey.equals(converterKey);
        }

        OracleConverter<Object, Object> getConverter() {

            return this.converterFactory == null
                ? this.converter
                : this.converterFactory;
        }

    }

}