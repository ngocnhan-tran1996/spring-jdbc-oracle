package io.spring.jdbc.oracle.mapper;

import io.spring.jdbc.oracle.accessor.ClassRecord;
import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import io.spring.jdbc.oracle.converter.support.DefaultOracleConverters;
import io.spring.jdbc.oracle.converter.support.NoneOracleConverter;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.property.MapperProperty;
import io.spring.jdbc.oracle.mapper.property.TypeProperty;
import io.spring.jdbc.oracle.mapper.property.TypeProperty.Types;
import io.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.spring.jdbc.oracle.parameter.output.ParameterOutput;
import io.spring.jdbc.oracle.utils.Mappers;
import io.spring.jdbc.oracle.utils.Matchers;
import io.spring.jdbc.oracle.utils.Strings;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.ReflectionUtils;

class BeanPropertyMapper<S> extends AbstractMapper {

    static final String UNIQUE_NAME = "Field name must be unique";

    private final Map<String, TypeProperty> readProperties = new LinkedCaseInsensitiveMap<>();
    private final Map<String, TypeProperty> writeProperties = new LinkedCaseInsensitiveMap<>();
    private final Class<S> mappedClass;
    private final List<MapperProperty> mapperProperties;
    private OracleConverters converters = DefaultOracleConverters.INSTANCE;

    BeanPropertyMapper(Class<S> mappedClass) {

        this.mappedClass = new ClassRecord<>(mappedClass).mappedClass();
        this.mapperProperties = Stream.of(BeanUtils.getPropertyDescriptors(mappedClass))
            .map(
                propertyDescriptor -> {

                    var field = ReflectionUtils.findField(
                        mappedClass,
                        propertyDescriptor.getName()
                    );

                    return new MapperProperty(field, propertyDescriptor);
                }
            )
            .filter(mapperProperty -> Objects.nonNull(mapperProperty.field()))
            .toList();
    }

    public static <S> BeanPropertyMapper<S> newInstance(Class<S> mappedClass) {

        return new BeanPropertyMapper<>(mappedClass)
            .extractProperties();
    }

    BeanPropertyMapper<S> extractProperties() {

        for (var property : this.mapperProperties) {

            var field = property.field();
            var pd = property.propertyDescriptor();

            var name = pd.getName();
            var oracleParameter = field.getDeclaredAnnotation(OracleParameter.class);
            var columnName = Optional.ofNullable(oracleParameter)
                .map(OracleParameter::value)
                .filter(Strings::isNotBlank)
                .filter(Predicate.not(name::equalsIgnoreCase))
                .orElse(name);

            if (this.readProperties.containsKey(columnName)) {

                throw new ValueException(UNIQUE_NAME);
            }

            if (pd.getReadMethod() != null) {

                var typeProperty = this.getTypeProperty(name, OracleParameter::input)
                    .apply(oracleParameter);
                this.readProperties.put(columnName, typeProperty);
            }

            this.doExtractProperties(pd, columnName, oracleParameter);
        }

        return this;
    }

    void doExtractProperties(
        PropertyDescriptor pd,
        String columnName,
        OracleParameter oracleParameter) {

        if (this.writeProperties.containsKey(columnName)) {

            throw new ValueException(UNIQUE_NAME);
        }

        if (pd.getWriteMethod() != null) {

            var typeProperty = this.getTypeProperty(pd.getName(), OracleParameter::output)
                .apply(oracleParameter);
            this.writeProperties.put(columnName, typeProperty);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Object[] toStruct(
        Connection connection,
        int columns,
        Map<String, Integer> columnNameByIndex,
        T source) {

        var bw = PropertyAccessorFactory.forBeanPropertyAccess(source);
        Object[] values = new Object[columns];

        this.readProperties.forEach((columnName, typeProperty) -> {

            if (Matchers.not(columnNameByIndex.containsKey(columnName))) {

                return;
            }

            var fieldName = typeProperty.getFieldName();
            var propertyType = bw.getPropertyType(fieldName);
            var value = bw.getPropertyValue(fieldName);

            values[columnNameByIndex.get(columnName)] = switch (typeProperty.getType()) {

                case STRUCT ->
                    ParameterInput.withParameterName(fieldName, (Class<Object>) propertyType)
                        .withValue(value)
                        .withStruct(typeProperty.getStructName())
                        .convert(connection);

                case ARRAY ->
                    ParameterInput.withParameterName(fieldName, (Class<Object>) propertyType)
                        .withValues(Mappers.toArray(value))
                        .withArray(typeProperty.getArrayName())
                        .convert(connection);

                case STRUCT_ARRAY -> {

                    propertyType = Mappers.extractClassFromArray(
                        bw.getPropertyTypeDescriptor(fieldName)
                    );
                    yield ParameterInput.withParameterName(fieldName, (Class<Object>) propertyType)
                        .withValues(Mappers.toArray(value))
                        .withStructArray(typeProperty.getArrayName(), typeProperty.getStructName())
                        .convert(connection);
                }

                case CONVERTER -> Mappers.convertValue(typeProperty, value);

                default -> {

                    var jdbcClass = this.converters.determineJavaClassForJdbcTypeCode(propertyType);
                    yield this.converters.convert(
                        value,
                        TypeDescriptor.valueOf(propertyType),
                        TypeDescriptor.valueOf(jdbcClass)
                    );
                }
            };

        });

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T constructInstance(Connection connection, Map<String, Object> valueByName) {

        var instance = BeanUtils.instantiateClass(this.mappedClass);
        var bw = PropertyAccessorFactory.forBeanPropertyAccess(instance);

        this.writeProperties.forEach((columnName, typeProperty) -> {

            if (Matchers.not(valueByName.containsKey(columnName))) {

                return;
            }

            var fieldName = typeProperty.getFieldName();
            var propertyType = bw.getPropertyType(fieldName);
            var rawValue = valueByName.get(columnName);

            Object value = this.constructValue(
                typeProperty,
                fieldName,
                propertyType,
                connection,
                rawValue,
                bw.getPropertyTypeDescriptor(fieldName)
            );
            bw.setPropertyValue(fieldName, value);
        });

        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    Function<OracleParameter, TypeProperty> getTypeProperty(
        String fieldName,
        Function<OracleParameter, OracleType> oracleTypeFunction) {

        return oracleParameter -> {

            var typeProperty = new TypeProperty();
            typeProperty.setFieldName(fieldName);
            Optional.ofNullable(oracleParameter)
                .map(oracleTypeFunction)
                .ifPresent(
                    oracleTypeValue -> {

                        var structName = oracleTypeValue.structName();
                        var arrayName = oracleTypeValue.arrayName();
                        var converter = (Class<? extends OracleConverter<Object, Object>>) oracleTypeValue.converter();

                        if (Strings.isNotBlank(structName)) {

                            typeProperty.setType(Types.STRUCT);
                            typeProperty.setStructName(structName.toUpperCase());
                        }

                        if (Strings.isNotBlank(arrayName)) {

                            var type = typeProperty.getType() == Types.STRUCT
                                ? Types.STRUCT_ARRAY
                                : Types.ARRAY;
                            typeProperty.setType(type);
                            typeProperty.setArrayName(arrayName.toUpperCase());
                        }

                        if (typeProperty.getType() != Types.NONE
                            || NoneOracleConverter.class.isAssignableFrom(converter)) {

                            return;
                        }

                        typeProperty.setType(Types.CONVERTER);
                        typeProperty.setConverter(converter);
                    }
                );

            return typeProperty;
        };
    }

    Object constructValue(
        TypeProperty typeProperty,
        String fieldName,
        Class<?> targetType,
        Connection connection,
        Object rawValue,
        TypeDescriptor typeDescriptor) {

        return switch (typeProperty.getType()) {

            case STRUCT -> ParameterOutput.withParameterName(fieldName, targetType)
                .withStruct(typeProperty.getStructName())
                .convert(connection, rawValue);

            case ARRAY -> {

                var value = ParameterOutput.withParameterName(fieldName, targetType)
                    .withArray(typeProperty.getArrayName())
                    .convert(connection, rawValue);
                yield this.converters.convert(
                    value,
                    TypeDescriptor.forObject(value),
                    typeDescriptor
                );
            }

            case STRUCT_ARRAY -> ParameterOutput.withParameterName(
                    fieldName,
                    Mappers.extractClassFromArray(typeDescriptor)
                )
                .withStructArray(typeProperty.getArrayName())
                .convert(connection, rawValue);

            case CONVERTER -> Mappers.convertValue(typeProperty, rawValue);

            default -> this.converters.convert(
                rawValue,
                TypeDescriptor.forObject(rawValue),
                typeDescriptor
            );
        };

    }

    Class<S> getMappedClass() {

        return this.mappedClass;
    }

    void setConverters(OracleConverters converters) {

        if (converters != null) {

            this.converters = converters;
        }

    }

}