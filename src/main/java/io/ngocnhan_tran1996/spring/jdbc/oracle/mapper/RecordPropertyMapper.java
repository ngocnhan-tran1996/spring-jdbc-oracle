package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.LinkedCaseInsensitiveMap;

class RecordPropertyMapper<T> extends BeanPropertyMapper<T> {

    private final Constructor<T> constructor;
    private final Map<String, String> parameterByFieldName = new LinkedCaseInsensitiveMap<>();

    private RecordPropertyMapper(Class<T> mappedClass) {

        super(mappedClass);

        this.constructor = BeanUtils.getResolvableConstructor(super.getMappedClass());
        int paramCount = this.constructor.getParameterCount();
        if (paramCount < 1) {

            throw new ValueException("Record must have parameters");
        }
    }

    @Override
    RecordPropertyMapper<T> extractParameterNames() {

        for (var property : super.getMapperProperties()) {

            var field = property.field();
            var pd = property.propertyDescriptor();

            var name = pd.getName();
            var oracleParameterName = field.getDeclaredAnnotation(OracleParameter.class);
            var propertyName = Optional.ofNullable(oracleParameterName)
                .map(OracleParameter::value)
                .filter(Predicate.not(Strings::isBlank))
                .filter(Predicate.not(name::equalsIgnoreCase))
                .orElse(name);

            this.parameterByFieldName.put(name, propertyName);
        }

        return this;
    }

    public static <T> RecordPropertyMapper<T> newInstance(Class<T> mappedClass) {

        return new RecordPropertyMapper<>(mappedClass)
            .extractParameterNames();
    }

    @Override
    protected T constructInstance(Map<String, Object> valueByName) {

        var args = new Object[this.constructor.getParameterCount()];
        var bw = new BeanWrapperImpl();

        int i = 0;
        for (var parameter : this.constructor.getParameters()) {

            var fieldName = this.parameterByFieldName.get(parameter.getName());

            if (fieldName == null) {

                continue;
            }

            args[i] = bw.convertIfNecessary(valueByName.get(fieldName), parameter.getType());
            i++;
        }

        return BeanUtils.instantiateClass(this.constructor, args);
    }

}