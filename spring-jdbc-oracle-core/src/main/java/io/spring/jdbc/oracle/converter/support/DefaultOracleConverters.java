package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.utils.Validators;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
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
    private final Map<Class<?>, Integer> javaClassToJdbcTypeCodeMap = new ConcurrentHashMap<>();
    private final Map<Integer, Class<?>> jdbcTypeCodeToJavaClassMap = new ConcurrentHashMap<>();

    private DefaultOracleConverters() {

        addDefaultConverters(this);
        this.javaClassToJdbcTypeCodeMap.putAll(buildJavaClassToJdbcTypeCodeMappings());
        this.jdbcTypeCodeToJavaClassMap.putAll(buildJdbcTypeCodeToJavaClassMappings());
    }

    private static void addDefaultConverters(OracleConverters converters) {

        converters.addGenericConverter(new NumberToStringGenericOracleConverter());
        converters.addGenericConverter(
            new CollectionToCollectionGenericOracleConverter(converters)
        );
        converters.addGenericConverter(
            new CollectionToArrayGenericOracleConverter(converters)
        );
        converters.addGenericConverter(
            new ArrayToCollectionGenericOracleConverter(converters)
        );
        converters.addGenericConverter(
            new ArrayToArrayGenericOracleConverter(converters)
        );

        converters.addConverter(new LocalDatetimeToTimestampOracleConverter());
        converters.addConverter(new TimestampToLocalDatetimeOracleConverter());
    }

    // FIXME need add more
    private static ConcurrentHashMap<Class<?>, Integer> buildJavaClassToJdbcTypeCodeMappings() {

        final ConcurrentHashMap<Class<?>, Integer> workMap = new ConcurrentHashMap<>();

        workMap.put(LocalDateTime.class, Types.TIMESTAMP);

        return workMap;
    }

    private static ConcurrentHashMap<Integer, Class<?>> buildJdbcTypeCodeToJavaClassMappings() {

        final ConcurrentHashMap<Integer, Class<?>> workMap = new ConcurrentHashMap<>();

        workMap.put(Types.TIMESTAMP, Timestamp.class);

        return workMap;
    }

    @Override
    public void addGenericConverter(GenericOracleConverter genericOracleConverter) {

        var convertKey = Validators.requireNotNull(genericOracleConverter, "GenericOracleConverter")
            .getConvertKey();
        if (convertKey == null) {

            throw new ValueException(DETERMINE_EXCEPTION.formatted("GenericOracleConverter"));
        }

        var existConvertKey = this.genericConverters.stream()
            .anyMatch(genericConverter -> genericConverter.existConvertKey(convertKey));
        if (existConvertKey) {

            return;
        }

        this.genericConverters.add(new ConvertAdapter(genericOracleConverter, convertKey));
    }

    @Override
    public void addConverter(OracleConverter<?, ?> converter) {

        Validators.requireNotNull(converter, "OracleConverter");

        if (converter instanceof GenericOracleConverter) {

            throw new ValueException(DETERMINE_EXCEPTION.formatted("OracleConverter"));
        }

        ResolvableType resolvableType = ResolvableType.forClass(converter.getClass())
            .as(OracleConverter.class);
        ResolvableType[] generics = resolvableType.getGenerics();
        Class<?> sourceType = generics[0].resolve();
        Class<?> targetType = generics[1].resolve();
        var convertKey = new ConvertKey(sourceType, targetType);

        if (this.converterCaches.containsKey(convertKey)) {

            return;
        }

        this.converterCaches.put(convertKey, new ConvertAdapter(converter, convertKey));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        return this.find(sourceType, targetType)
            .getConverter()
            .convert(source);
    }

    @Override
    public Class<?> determineJavaClassForJdbcTypeCode(Class<?> sourceType) {

        if (sourceType == null) {

            return null;
        }

        var typeCode = this.javaClassToJdbcTypeCodeMap.get(sourceType);

        if (typeCode == null) {

            return null;
        }

        return this.jdbcTypeCodeToJavaClassMap.get(typeCode);
    }

    private ConvertAdapter find(TypeDescriptor sourceType, TypeDescriptor targetType) {

        if (sourceType == null || targetType == null) {

            return ConvertAdapter.NONE;
        }

        List<Class<?>> sourceCandidates = this.getClassHierarchy(sourceType.getType());
        List<Class<?>> targetCandidates = this.getClassHierarchy(targetType.getType());

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

    private List<Class<?>> getClassHierarchy(Class<?> type) {

        List<Class<?>> hierarchy = new ArrayList<>(20);
        Set<Class<?>> visited = HashSet.newHashSet(20);
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

            if (superclass != null && superclass != Object.class) {

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

    private static final class ConvertAdapter {

        private static final ConvertAdapter NONE = new ConvertAdapter(
            new NoneOracleConverter(),
            new ConvertKey(Object.class, Object.class)
        );

        private final OracleConverter<Object, Object> converter;
        private final ConvertKey converterKey;

        @SuppressWarnings("unchecked")
        ConvertAdapter(OracleConverter<?, ?> converter, ConvertKey converterKey) {

            this.converter = (OracleConverter<Object, Object>) converter;
            this.converterKey = converterKey;
        }

        boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

            return this.converter instanceof GenericOracleConverter genericOracleConverter
                && genericOracleConverter.matches(sourceType, targetType);
        }

        boolean existConvertKey(ConvertKey converterKey) {

            return this.converterKey.equals(converterKey);
        }

        OracleConverter<Object, Object> getConverter() {

            return this.converter;
        }

    }

}