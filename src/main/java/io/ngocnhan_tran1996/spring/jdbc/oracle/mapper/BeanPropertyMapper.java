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
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.Struct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
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

    @Override
    protected <T> Object[] toStruct(
        Connection connection,
        int columns,
        Map<String, Integer> columnNameByIndex,
        T source) {

        var bw = new BeanWrapperImpl(source);
        Object[] values = new Object[columns];

        this.readProperties.forEach((columnName, typeProperty) -> {

            if (not(columnNameByIndex.containsKey(columnName))) {

                return;
            }

            var fieldName = typeProperty.getFieldName();
            var value = bw.getPropertyValue(fieldName);
            switch (typeProperty.getType()) {

                case STRUCT -> {

                    var mapper = DelegateMapper.newInstance(bw.getPropertyType(fieldName)).get();
                    values[columnNameByIndex.get(columnName)] = mapper.toStruct(
                        connection,
                        typeProperty.getStructName(),
                        value
                    );
                }

                case CONVERTER -> BeanUtils.findMethod(
                    typeProperty.getConverter(),
                    "convert",
                    bw.getPropertyType(fieldName)
                );

                default -> values[columnNameByIndex.get(columnName)] = value;
            }

        });

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T constructInstance(Connection connection, Map<String, Object> valueByName) {

        var instance = BeanUtils.instantiateClass(this.mappedClass);
        var bw = new BeanWrapperImpl(instance);

        this.writeProperties.forEach((columnName, typeProperty) -> {

            if (not(valueByName.containsKey(columnName))) {

                return;
            }

            var rawValue = valueByName.get(columnName);
            var fieldName = typeProperty.getFieldName();
            Object value;
            switch (typeProperty.getType()) {

                case STRUCT -> {

                    var mapper = DelegateMapper.newInstance(bw.getPropertyType(fieldName)).get();
                    value = mapper.fromStruct(connection, (Struct) rawValue);
                }

                case CONVERTER ->
                    value = this.convertValue(rawValue, bw.getPropertyType(fieldName));

                default -> value = rawValue;
            }

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

                        if (Strings.isBlank(structName)) {

                            if (NoneConverter.class.isAssignableFrom(converter)) {

                                return;
                            }

                            typeProperty.setType(Types.CONVERTER);
                            typeProperty.setConverter(converter);
                            return;
                        }

                        typeProperty.setType(Types.STRUCT);
                        typeProperty.setStructName(structName.toUpperCase());

                        if (Strings.isNotBlank(arrayName)) {

                            typeProperty.setType(Types.ARRAY);
                            typeProperty.setArrayName(arrayName.toUpperCase());
                        }

                    }
                );

            return typeProperty;
        };
    }

    Object convertValue(Object value, Class<?> targetType) {

        var sourceType = Optional.ofNullable(value)
            .map(Object::getClass)
            .orElse(null);
        return converters.convert(value, sourceType, targetType);
    }

    Class<S> getMappedClass() {

        return this.mappedClass;
    }

}