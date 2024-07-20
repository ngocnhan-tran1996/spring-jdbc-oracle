package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ClassRecord;
import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleType;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverters;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.DefaultOracleConverters;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.NoneConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.MapperProperty;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.TypeProperty;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.TypeProperty.Types;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output.ParameterOutput;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.MapperUtils;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
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

    private static final OracleConverters converters = DefaultOracleConverters.INSTANCE;
    private final Map<String, TypeProperty> readProperties = new LinkedCaseInsensitiveMap<>();
    private final Map<String, TypeProperty> writeProperties = new LinkedCaseInsensitiveMap<>();
    private final List<MapperProperty> mapperProperties;
    private final Class<S> mappedClass;

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

                throw new ValueException("Field name must be unique");
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

            if (not(columnNameByIndex.containsKey(columnName))) {

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

                case ARRAY -> ParameterInput.withParameterName(fieldName)
                    .withValues(MapperUtils.toArrayOrNull(value))
                    .withArray(typeProperty.getArrayName())
                    .convert(connection);

                case STRUCT_ARRAY -> {

                    propertyType = MapperUtils.extractClass(
                        bw.getPropertyTypeDescriptor(fieldName)
                    );
                    yield ParameterInput.withParameterName(fieldName, (Class<Object>) propertyType)
                        .withValues(MapperUtils.toArrayOrNull(value))
                        .withStructArray(typeProperty.getArrayName(), typeProperty.getStructName())
                        .convert(connection);
                }

                case CONVERTER -> BeanUtils.findMethod(
                    typeProperty.getConverter(),
                    "convert",
                    propertyType
                );

                default -> value;
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

            if (not(valueByName.containsKey(columnName))) {

                return;
            }

            var fieldName = typeProperty.getFieldName();
            var propertyType = bw.getPropertyType(fieldName);
            var rawValue = valueByName.get(columnName);

            Object value = this.constructValue(
                typeProperty,
                fieldName,
                (Class<Object>) propertyType,
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
                            || NoneConverter.class.isAssignableFrom(converter)) {

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
        Class<Object> targetType,
        Connection connection,
        Object rawValue,
        TypeDescriptor typeDescriptor) {

        return switch (typeProperty.getType()) {

            case STRUCT -> ParameterOutput.withParameterName(fieldName, targetType)
                .withStruct(typeProperty.getStructName())
                .convert(connection, rawValue);

            case ARRAY -> ParameterOutput.withParameterName(fieldName)
                .withArray(typeProperty.getArrayName())
                .convert(connection, rawValue);

            case STRUCT_ARRAY -> ParameterOutput.withParameterName(
                    fieldName,
                    MapperUtils.extractClass(typeDescriptor)
                )
                .withStructArray(typeProperty.getArrayName())
                .convert(connection, rawValue);

            case CONVERTER -> BeanUtils.findMethod(
                typeProperty.getConverter(),
                "convert",
                targetType
            );

            default -> {

                var sourceType = Optional.ofNullable(rawValue)
                    .map(Object::getClass)
                    .orElse(null);
                yield converters.convert(rawValue, sourceType, targetType);
            }
        };

    }

    Class<S> getMappedClass() {

        return this.mappedClass;
    }

}