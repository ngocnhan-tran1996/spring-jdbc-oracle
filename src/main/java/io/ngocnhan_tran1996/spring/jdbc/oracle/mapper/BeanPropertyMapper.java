package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ClassRecord;
import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.beans.PropertyDescriptor;
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

    private final Map<String, String> readProperties = new LinkedCaseInsensitiveMap<>();
    private final Map<String, String> writeProperties = new LinkedCaseInsensitiveMap<>();
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

            // FIXME add converter
            this.doExtractProperties(pd, propertyName);
        }

        return this;
    }

    void doExtractProperties(PropertyDescriptor pd, String propertyName) {

        if (pd.getWriteMethod() != null) {

            this.writeProperties.put(propertyName, pd.getName());
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

        this.writeProperties.forEach((columnName, fieldName) -> {

            if (not(valueByName.containsKey(columnName))) {

                return;
            }

            bw.setPropertyValue(fieldName, valueByName.get(columnName));
        });

        return instance;
    }

    public Class<T> getMappedClass() {

        return this.mappedClass;
    }

}