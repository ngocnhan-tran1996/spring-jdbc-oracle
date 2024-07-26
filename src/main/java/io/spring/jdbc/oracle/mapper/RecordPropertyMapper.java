package io.spring.jdbc.oracle.mapper;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.property.TypeProperty;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.LinkedCaseInsensitiveMap;

class RecordPropertyMapper<S> extends BeanPropertyMapper<S> {

    private final Map<String, TypeProperty> parameterByFieldName = new LinkedCaseInsensitiveMap<>();
    private final Constructor<S> constructor;
    private final TypeDescriptor[] constructorParameterTypes;

    private RecordPropertyMapper(Class<S> mappedClass) {

        super(mappedClass);

        this.constructor = BeanUtils.getResolvableConstructor(super.getMappedClass());
        int paramCount = this.constructor.getParameterCount();
        if (paramCount < 1) {

            throw new ValueException("Record must have parameters");
        }

        this.constructorParameterTypes = new TypeDescriptor[paramCount];
        for (int i = 0; i < paramCount; ++i) {

            var methodParameter = new MethodParameter(this.constructor, i);
            this.constructorParameterTypes[i] = new TypeDescriptor(methodParameter);
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

        if (this.parameterByFieldName.containsKey(columnName)) {

            throw new ValueException(UNIQUE_NAME);
        }

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
                this.constructorParameterTypes[i]
            );

            args[i] = bw.convertIfNecessary(value, targetType);
            i++;
        }

        return (T) BeanUtils.instantiateClass(this.constructor, args);
    }

}