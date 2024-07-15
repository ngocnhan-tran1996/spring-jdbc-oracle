package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverterFactory;
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
import org.springframework.util.ClassUtils;

public final class DefaultOracleConverters implements OracleConverters {

    public static final OracleConverters INSTANCE = new DefaultOracleConverters();

    private final Set<ConvertAdapter> globalConverters = new CopyOnWriteArraySet<>();
    private final Map<ConvertKey, ConvertAdapter> converterCaches = new ConcurrentHashMap<>();

    private DefaultOracleConverters() {

        addDefaultConverters(this);
    }

    private static void addDefaultConverters(DefaultOracleConverters converters) {

//        converters.addConverterFactory(new NumberToNumberConverterFactory());
//        converters.addConverter(new NumberToStringConverter());
    }

    @Override
    public void addConverterFactory(OracleConverterFactory<?, ?> converterFactory) {

        if (converterFactory == null) {

            throw new ValueException("ConverterFactory must not be null");
        }

        var converterKey = this.getConvertKey(
            converterFactory.getClass(),
            OracleConverterFactory.class
        );
        if (converterKey == null) {

            throw new ValueException("Unable determine %s".formatted(converterFactory));
        }

        var existConvertKey = this.globalConverters.stream()
            .anyMatch(globalConverter -> globalConverter.existConvertKey(converterKey));
        if (not(existConvertKey)) {

            this.globalConverters.add(new ConvertAdapter(converterFactory, converterKey));
        }

    }

    @Override
    public void addConverter(OracleConverter<?, ?> converter) {

        if (converter == null) {

            throw new ValueException("Converter must not be null");
        }

        var converterKey = this.getConvertKey(converter.getClass(), OracleConverter.class);
        if (converterKey == null) {

            throw new ValueException("Unable determine %s".formatted(converter));
        }

        this.converterCaches.put(converterKey, new ConvertAdapter(converter));
    }

    @Override
    public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {

        return this.find(sourceType, targetType)
            .getConverter(targetType)
            .convert(source);
    }

    private ConvertKey getConvertKey(Class<?> converterClass, Class<?> genericClass) {

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

    private ConvertAdapter find(Class<?> sourceType, Class<?> targetType) {

        if (sourceType == null || targetType == null) {

            return ConvertAdapter.NONE;
        }

        List<Class<?>> sourceCandidates = this.getClassHierarchy(sourceType);
        List<Class<?>> targetCandidates = this.getClassHierarchy(targetType);

        for (var sourceCandidate : sourceCandidates) {
            for (var targetCandidate : targetCandidates) {

                var converter = this.converterCaches.get(
                    new ConvertKey(sourceCandidate, targetCandidate)
                );

                if (converter != null) {

                    return converter;
                }

                for (var globalConverter : this.globalConverters) {

                    if (globalConverter.matches(sourceType, targetType)) {

                        return globalConverter;
                    }

                }

            }
        }

        return ConvertAdapter.NONE;
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

    private record ConvertKey(Class<?> sourceType, Class<?> targetType) {

    }

    private static final class ConvertAdapter {

        static final ConvertAdapter NONE = new ConvertAdapter(new NoneConverter());

        private final OracleConverter<Object, Object> converter;
        private final OracleConverterFactory<Object, Object> converterFactory;
        private final ConvertKey converterKey;

        @SuppressWarnings("unchecked")
        ConvertAdapter(OracleConverterFactory<?, ?> converterFactory, ConvertKey converterKey) {

            this.converter = null;
            this.converterFactory = (OracleConverterFactory<Object, Object>) converterFactory;
            this.converterKey = converterKey;
        }

        @SuppressWarnings("unchecked")
        ConvertAdapter(OracleConverter<?, ?> converter) {

            this.converter = (OracleConverter<Object, Object>) converter;
            this.converterFactory = null;
            this.converterKey = null;
        }

        boolean matches(Class<?> sourceType, Class<?> targetType) {

            return this.converterFactory != null
                && this.converterFactory.matches(sourceType, targetType);
        }

        boolean existConvertKey(ConvertKey converterKey) {

            return this.converterKey != null
                && this.converterKey.equals(converterKey);
        }

        @SuppressWarnings("unchecked")
        OracleConverter<Object, Object> getConverter(Class<?> targetType) {

            return this.converterFactory != null
                ? (OracleConverter<Object, Object>) this.converterFactory.getOracleConverter(
                targetType)
                : this.converter;
        }

    }

}