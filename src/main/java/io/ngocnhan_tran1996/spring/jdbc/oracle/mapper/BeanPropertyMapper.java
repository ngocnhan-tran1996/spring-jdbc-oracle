package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ClassRecord;
import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.NoneConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.OracleConverters;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.MapperProperty;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.WriteProperty;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.ReflectionUtils;

class BeanPropertyMapper<T> extends AbstractMapper<T> {

    private static final OracleConverters converters = OracleConverters.INSTANCE;
    private final Map<String, String> readProperties = new LinkedCaseInsensitiveMap<>();
    private final Map<String, WriteProperty> writeProperties = new LinkedCaseInsensitiveMap<>();
    private final List<MapperProperty> mapperProperties;
    private final Class<T> mappedClass;

    BeanPropertyMapper(Class<T> mappedClass) {

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

    public static <T> BeanPropertyMapper<T> newInstance(Class<T> mappedClass) {

        return new BeanPropertyMapper<>(mappedClass)
            .extractProperties();
    }

    BeanPropertyMapper<T> extractProperties() {

        for (var property : this.mapperProperties) {

            var field = property.field();
            var pd = property.propertyDescriptor();

            var name = pd.getName();
            var oracleParameter = field.getDeclaredAnnotation(OracleParameter.class);
            var propertyName = Optional.ofNullable(oracleParameter)
                .map(OracleParameter::value)
                .filter(Predicate.not(Strings::isBlank))
                .filter(Predicate.not(name::equalsIgnoreCase))
                .orElse(name);

            if (this.readProperties.containsKey(propertyName)) {

                throw new ValueException("Field name must be unique");
            }

            if (pd.getReadMethod() != null) {

                this.readProperties.put(propertyName, name);
            }

            var convertMethod = this.findMethod(pd, oracleParameter);
            var writeProperty = new WriteProperty(propertyName, name, convertMethod);
            this.doExtractProperties(pd, writeProperty);
        }

        return this;
    }

    void doExtractProperties(PropertyDescriptor pd, WriteProperty writeProperty) {

        if (pd.getWriteMethod() != null) {

            this.writeProperties.put(writeProperty.propertyName(), writeProperty);
        }

    }

    @Override
    protected Object[] toStruct(int columns, Map<String, Integer> columnNameByIndex, T source) {

        var bw = new BeanWrapperImpl(source);
        Object[] values = new Object[columns];

        this.readProperties.forEach((columnName, fieldName) -> {

            if (not(columnNameByIndex.containsKey(columnName))) {

                return;
            }

            values[columnNameByIndex.get(columnName)] = bw.getPropertyValue(fieldName);
        });

        return values;
    }

    @Override
    protected T constructInstance(Map<String, Object> valueByName) {

        var instance = BeanUtils.instantiateClass(this.mappedClass);
        var bw = new BeanWrapperImpl(instance);

        this.writeProperties.forEach((columnName, writeProperty) -> {

            if (not(valueByName.containsKey(columnName))) {

                return;
            }

            var rawValue = valueByName.get(columnName);
            var fieldName = writeProperty.fieldName();
            var value = Optional.ofNullable(writeProperty.convertMethod())
                .map(method -> ReflectionUtils.invokeMethod(method, rawValue))
                .orElseGet(() -> this.convertValue(rawValue, bw.getPropertyType(fieldName)));

            bw.setPropertyValue(fieldName, value);
        });

        return instance;
    }

    private Method findMethod(PropertyDescriptor pd, OracleParameter oracleParameter) {

        return Optional.ofNullable(oracleParameter)
            .map(OracleParameter::converter)
            .filter(Predicate.not(NoneConverter.class::isAssignableFrom))
            .map(aClass -> BeanUtils.findMethod(aClass, "convert", pd.getPropertyType()))
            .orElse(null);
    }

    Object convertValue(Object value, Class<?> targetType) {

        var sourceType = Optional.ofNullable(value)
            .map(Object::getClass)
            .orElse(null);
        return converters.find(sourceType, targetType)
            .convert(value);
    }

    Class<T> getMappedClass() {

        return this.mappedClass;
    }

}