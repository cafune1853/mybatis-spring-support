package com.github.cafune1853.mybatis.spring.support.util;

import lombok.Getter;

@Getter
public class RepresentFieldTypeValue {
    private final Object value;
    private final Class<?> type;

    public RepresentFieldTypeValue(Object value, Class<?> type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getType() {
        return type;
    }
}
