package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

class RecordPropertyMapper<T> extends BeanPropertyMapper<T> {

    private final Constructor<T> constructor;

    private RecordPropertyMapper(Class<T> mappedClass) {

        super(mappedClass);

        this.constructor = BeanUtils.getResolvableConstructor(super.getMappedClass());
        int paramCount = this.constructor.getParameterCount();
        if (paramCount < 1) {

            throw new ValueException("Record must have parameters");
        }

        super.extractParameterNames();
    }

    public static <T> RecordPropertyMapper<T> newInstance(Class<T> mappedClass) {

        return new RecordPropertyMapper<>(mappedClass);
    }

    @Override
    protected T constructInstance(Map<String, Object> valueByName) {

        var args = new ArrayList<>();
        var bw = new BeanWrapperImpl();

        super.getReadProperties().forEach((fieldName, pd) -> {

            var invalidName = not(valueByName.containsKey(fieldName))
                && not(valueByName.containsKey(pd.getName()));
            if (invalidName) {

                return;
            }

            args.add(bw.convertIfNecessary(valueByName.get(fieldName), pd.getPropertyType()));
        });

        if (args.size() != this.constructor.getParameterCount()) {

            throw new ValueException("Record must same parameters");
        }

        return BeanUtils.instantiateClass(this.constructor, args.toArray());
    }

}