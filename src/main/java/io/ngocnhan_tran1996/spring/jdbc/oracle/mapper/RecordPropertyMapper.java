package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.WriteProperty;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.ReflectionUtils;

class RecordPropertyMapper<T> extends BeanPropertyMapper<T> {

    private final Constructor<T> constructor;
    private final Map<String, WriteProperty> parameterByFieldName = new LinkedCaseInsensitiveMap<>();

    private RecordPropertyMapper(Class<T> mappedClass) {

        super(mappedClass);

        this.constructor = BeanUtils.getResolvableConstructor(super.getMappedClass());
        int paramCount = this.constructor.getParameterCount();
        if (paramCount < 1) {

            throw new ValueException("Record must have parameters");
        }

        super.extractProperties();
    }

    public static <T> RecordPropertyMapper<T> newInstance(Class<T> mappedClass) {

        return new RecordPropertyMapper<>(mappedClass);
    }

    @Override
    void doExtractProperties(PropertyDescriptor pd, WriteProperty writeProperty) {

        this.parameterByFieldName.put(pd.getName(), writeProperty);
    }

    @Override
    protected T constructInstance(Map<String, Object> valueByName) {

        var args = new Object[this.constructor.getParameterCount()];
        var bw = new BeanWrapperImpl();

        int i = 0;
        for (var parameter : this.constructor.getParameters()) {

            var targetType = parameter.getType();
            var writeProperty = this.parameterByFieldName.get(parameter.getName());
            var rawValue = valueByName.get(writeProperty.propertyName());
            var value = Optional.ofNullable(writeProperty.convertMethod())
                .map(method -> ReflectionUtils.invokeMethod(method, rawValue))
                .orElseGet(() -> this.convertValue(rawValue, targetType));

            args[i] = bw.convertIfNecessary(value, targetType);
            i++;
        }

        return BeanUtils.instantiateClass(this.constructor, args);
    }

}