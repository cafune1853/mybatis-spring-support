package com.github.cafune1853.mybatis.support.meta;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 用于解析并保存实体类的相关元数据信息。
 * @author huangzhw
 */
@Slf4j
public final class EntityMeta {
    private final Class<?> entityClazz;
    private final String tableName;
    private final String idColumnName;
    private final Field idField;
    private final Map<String, Field> columnFieldMaps;

    EntityMeta(Class<?> entityClazz, String tableName, String idColumnName, Field idField, Map<String, Field> columnFieldMaps) {
        if (entityClazz == null) {
            throw new IllegalArgumentException("EntityClazz should not be null.");
        }
        this.entityClazz = entityClazz;
        this.tableName = tableName;
        this.idColumnName = idColumnName;
        this.idField = idField;
        this.columnFieldMaps = Collections.unmodifiableMap(columnFieldMaps);
    }

    public String columnNameToFieldName(String columnName) {
        Field field = columnFieldMaps.get(columnName);
        if (field == null) {
            throw new IllegalArgumentException(String.format("ColumnName:%s is not a correct columnName.", columnName));
        }
        return field.getName();
    }

    public Class<?> getEntityClazz() {
        return entityClazz;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public Field getIdField() {
        return idField;
    }

    public Map<String, Field> getColumnFieldMaps() {
        return columnFieldMaps;
    }
}
