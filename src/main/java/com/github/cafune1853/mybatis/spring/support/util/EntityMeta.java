package com.github.cafune1853.mybatis.spring.support.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.extern.slf4j.Slf4j;

/**
 * 用于解析并保存实体类的相关元数据信息。
 * @author huangzhw
 */
@Slf4j
public final class EntityMeta {
    private static final int EXCLUDE_MODIFIERS = Modifier.TRANSIENT | Modifier.STATIC;
    private static final Map<Class<?>, EntityMeta> PERSISTENCE_ENTITY_META_CACHE = new ConcurrentHashMap<>();
    private final Class<?> entityClazz;
    private final String tableName;
    private final String idColumnName;
    private final Field idField;
    private final Map<String, Field> columnFieldMaps;

    private EntityMeta(Class<?> entityClazz) {
        if (entityClazz == null) {
            throw new IllegalArgumentException("EntityClazz should not be null.");
        }
        this.entityClazz = entityClazz;
        EntityAnalyzeResult entityAnalyzeResult = analyzeEntity(this.entityClazz);
        this.tableName = entityAnalyzeResult.getTableName();
        this.idColumnName = entityAnalyzeResult.getIdColumnName();
        this.idField = entityAnalyzeResult.getIdField();
        this.columnFieldMaps = Collections.unmodifiableMap(entityAnalyzeResult.getColumnFieldMap());
    }

    public static EntityMeta getPersistenceEntityMeta(Class<?> clazz) {
        return PERSISTENCE_ENTITY_META_CACHE.computeIfAbsent(clazz, EntityMeta::new);
    }

    private static EntityAnalyzeResult analyzeEntity(Class<?> entityClazz) {
        String tableName = getTableName(entityClazz);
        String idColumnName = null;
        Field idField = null;
        Map<String, Field> columnFieldMap = new HashMap<>(10);
        for (Class<?> curClazz = entityClazz; curClazz != Object.class; curClazz = curClazz.getSuperclass()) {
            Field[] fields = curClazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Transient.class) && ((field.getModifiers() & EXCLUDE_MODIFIERS) == 0)) {
                    String columnName = null;
                    if(field.isAnnotationPresent(Column.class)){
                        columnName = field.getAnnotation(Column.class).name();
                    }
                    if(columnName == null){
                        columnName = field.getName();
                    }
                    columnName = StringUtil.camelCaseToUnderScore(columnName);
                    if (columnName.contains("$")) {
                        continue;
                    }
                    columnFieldMap.putIfAbsent(columnName, field);
                    if (field.isAnnotationPresent(Id.class)) {
                        idColumnName = columnName;
                        idField = field;
                    }
                    if (idColumnName == null && "id".equals(columnName)) {
                        idColumnName = columnName;
                        idField = field;
                    }
                }
            }
        }
        EntityAnalyzeResult entityAnalyzeResult = new EntityAnalyzeResult();
        entityAnalyzeResult.setTableName(tableName).setIdColumnName(idColumnName).setIdField(idField).setColumnFieldMap(columnFieldMap);
        return entityAnalyzeResult;
    }

    private static String getTableName(Class<?> entityClazz) {
        String tableName = null;
        if (entityClazz.isAnnotationPresent(Table.class)) {
            tableName = entityClazz.getAnnotation(Table.class).name();
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            tableName = entityClazz.getSimpleName();
        }
        return StringUtil.camelCaseToUnderScore(tableName);
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

    private static class EntityAnalyzeResult {
        private String tableName;
        private String idColumnName;
        private Field idField;
        private Map<String, Field> columnFieldMap;

        private String getTableName() {
            return tableName;
        }

        private EntityAnalyzeResult setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        private String getIdColumnName() {
            return idColumnName;
        }

        private EntityAnalyzeResult setIdColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
            return this;
        }

        private Field getIdField() {
            return idField;
        }

        private EntityAnalyzeResult setIdField(Field idField) {
            this.idField = idField;
            return this;
        }

        private Map<String, Field> getColumnFieldMap() {
            return columnFieldMap;
        }

        private EntityAnalyzeResult setColumnFieldMap(Map<String, Field> columnFieldMap) {
            this.columnFieldMap = columnFieldMap;
            return this;
        }
    }
}
