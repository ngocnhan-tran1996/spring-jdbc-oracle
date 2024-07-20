package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property.TypeProperty;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.LinkedCaseInsensitiveMap;

class RecordPropertyMapper<S> extends BeanPropertyMapper<S> {

    private final Constructor<S> constructor;
    private final Map<String, TypeProperty> parameterByFieldName = new LinkedCaseInsensitiveMap<>();

    private RecordPropertyMapper(Class<S> mappedClass) {

        super(mappedClass);

        this.constructor = BeanUtils.getResolvableConstructor(super.getMappedClass());
        int paramCount = this.constructor.getParameterCount();
        if (paramCount < 1) {

            throw new ValueException("Record must have parameters");
        }

        super.extractProperties();
    }

    public static <S> RecordPropertyMapper<S> newInstance(Class<S> mappedClass) {

        return new RecordPropertyMapper<>(mappedClass);
    }

    @Override
    void doExtractProperties(
        PropertyDescriptor pd,
        String columnName,
        OracleParameter oracleParameter) {

        // Record class will save columnName instead
        var typeProperty = super.getTypeProperty(columnName, OracleParameter::output)
            .apply(oracleParameter);
        this.parameterByFieldName.put(pd.getName(), typeProperty);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T constructInstance(Connection connection, Map<String, Object> valueByName) {

        var args = new Object[this.constructor.getParameterCount()];
        var bw = new BeanWrapperImpl();

        int i = 0;
        for (var parameter : this.constructor.getParameters()) {

            var targetType = parameter.getType();
            var fieldName = parameter.getName();
            var typeProperty = this.parameterByFieldName.get(fieldName);
            var rawValue = valueByName.get(typeProperty.getFieldName());

            Object value = this.constructValue(
                typeProperty,
                fieldName,
                targetType,
                connection,
                rawValue,
                TypeDescriptor.valueOf(parameter.getType())
            );
            args[i] = bw.convertIfNecessary(value, targetType);
            i++;
        }

        return (T) BeanUtils.instantiateClass(this.constructor, args);
    }

}