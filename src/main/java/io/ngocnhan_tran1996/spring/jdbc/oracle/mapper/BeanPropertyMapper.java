package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class BeanPropertyMapper<T> extends ClassMapper<T> {

    private final BeanWrapperImpl bw = new BeanWrapperImpl();
    private final Map<String, PropertyDescriptor> readProperties = new LinkedCaseInsensitiveMap<>();
    private final Map<String, PropertyDescriptor> writeProperties = new LinkedCaseInsensitiveMap<>();

    private BeanPropertyMapper(Class<T> mappedClass) {

        super.setMappedClass(mappedClass);

        var instance = BeanUtils.instantiateClass(mappedClass);
        bw.setBeanInstance(instance);
    }

    public static <T> BeanPropertyMapper<T> newInstance(Class<T> mappedClass) {

        return new BeanPropertyMapper<>(mappedClass)
            .extractParameterNames();
    }

    BeanPropertyMapper<T> extractParameterNames() {

        var mappedClass = super.getMappedClass();
        for (var field : mappedClass.getDeclaredFields()) {

            String name = field.getName();

            try {

                var oracleParameterName = field.getDeclaredAnnotation(OracleParameter.class);
                var propertyName = Optional.ofNullable(oracleParameterName)
                    .map(OracleParameter::value)
                    .filter(Predicate.not(Strings::isBlank))
                    .filter(Predicate.not(name::equalsIgnoreCase))
                    .orElse(name);

                var pd = new PropertyDescriptor(name, mappedClass);
                if (pd.getReadMethod() != null) {

                    this.readProperties.put(propertyName, pd);
                }

                if (pd.getWriteMethod() != null) {

                    this.writeProperties.put(propertyName, pd);
                }

            } catch (Exception ex) {

                this.log.debug("Can not find field %s".formatted(name), ex);
            }

        }

        return this;
    }

    @Override
    protected Object[] createStruct(int columns, Map<String, Integer> columnNameByIndex, T source) {

        this.bw.setBeanInstance(source);
        Object[] values = new Object[columns];

        this.readProperties.forEach((fieldName, pd) -> {

            if (not(columnNameByIndex.containsKey(fieldName))) {

                return;
            }

            String name = pd.getName();
            values[columnNameByIndex.get(fieldName)] = this.bw.getPropertyValue(name);
        });

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T constructInstance(Map<String, Object> valueByName) {

        this.writeProperties.forEach((fieldName, pd) -> {

            if (not(valueByName.containsKey(fieldName))) {

                return;
            }

            String name = pd.getName();
            this.bw.setPropertyValue(name, valueByName.get(fieldName));
        });

        return (T) this.bw.getWrappedInstance();
    }

}