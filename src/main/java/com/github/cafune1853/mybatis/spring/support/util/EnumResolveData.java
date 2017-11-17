package com.github.cafune1853.mybatis.spring.support.util;

import java.lang.reflect.Field;

import lombok.Data;

/**
 * @author huangzhw
 */
@Data
class EnumResolveData {
    private final boolean hasRepresentField;
    private final Field representField;
    private final Class<?> representFieldClass;
    private final Object[] enumValues;

	EnumResolveData(boolean hasRepresentField, Field representField, Class<?> representFieldClass, Object[] enumValues) {
        this.hasRepresentField = hasRepresentField;
        this.representField = representField;
        this.enumValues = enumValues;
        this.representFieldClass = representFieldClass;
    }

    public boolean isHasRepresentField() {
        return hasRepresentField;
    }

    public Field getRepresentField() {
        return representField;
    }

    public Object[] getEnumValues() {
        return enumValues;
    }
    
    public Class<?> getRepresentFieldClass() {
        return representFieldClass;
    }
}
