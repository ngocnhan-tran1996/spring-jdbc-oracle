package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;
import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class BeanPropertyMapper<T> extends AbstractMapper<T> {

    private final Class<T> mappedClass;
    private final Map<String, PropertyDescriptor> readProperties = new LinkedCaseInsensitiveMap<>();
    private final Map<String, PropertyDescriptor> writeProperties = new LinkedCaseInsensitiveMap<>();

    private BeanPropertyMapper(Class<T> mappedClass) {

        this.mappedClass = mappedClass;
    }

    public static <T> BeanPropertyMapper<T> newInstance(Class<T> mappedClass) {

        Objects.requireNonNull(mappedClass, NOT_NULL.formatted("mapped class"));
        return new BeanPropertyMapper<>(mappedClass)
            .extractParameterNames();
    }

    BeanPropertyMapper<T> extractParameterNames() {

        for (var pd : BeanUtils.getPropertyDescriptors(this.mappedClass)) {

            String name = pd.getName();

            try {

                var columnName = this.mappedClass.getDeclaredField(name)
                    .getDeclaredAnnotation(OracleParameter.class);
                var propertyName = Optional.ofNullable(columnName)
                    .map(OracleParameter::value)
                    .filter(Predicate.not(Strings::isBlank))
                    .filter(Predicate.not(name::equalsIgnoreCase))
                    .orElse(name);

                if (pd.getReadMethod() != null) {

                    this.readProperties.put(propertyName, pd);
                }

                if (pd.getWriteMethod() != null) {

                    this.writeProperties.put(propertyName, pd);
                }

            } catch (NoSuchFieldException ex) {

                this.log.debug("Can not find field %s".formatted(name), ex);
            }

        }

        return this;
    }

    @Override
    protected Object[] createStruct(int columns, Map<String, Integer> columnNameByIndex) {

        Object[] values = new Object[columns];

        this.readProperties.forEach((fieldName, pd) -> {

            if (not(columnNameByIndex.containsKey(fieldName))) {

                return;
            }

            String name = pd.getName();
            values[columnNameByIndex.get(fieldName)] = pd.getValue(name);
        });

        return values;
    }

}