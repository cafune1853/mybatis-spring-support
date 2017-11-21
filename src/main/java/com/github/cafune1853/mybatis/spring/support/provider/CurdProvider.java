package com.github.cafune1853.mybatis.spring.support.provider;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.github.cafune1853.mybatis.spring.support.config.DBConfig;
import com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper;
import com.github.cafune1853.mybatis.spring.support.util.EntityMeta;

import lombok.extern.slf4j.Slf4j;

/**
 * @author huangzhw
 */
@Slf4j
public class CurdProvider {
    public static final String CLASS_KEY = "CLAZZ";
    public static final String PARAM_KEY = "PARAM";
    public static final String PAGE_KEY = "PAGE";
    private static final String COLUMN_KEY = "column";
    private static final String WHERE_KEY = "where";
    private static final String ORDER_KEY = "order";
    private static final String GROUP_KEY = "groupBy";
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#insert(Object)
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#insertAndSetObjectId(Object)
     * @param entity: 实体对象
     */
    public String insert(Object entity) {
        EntityMeta meta = EntityMeta.getPersistenceEntityMeta(entity.getClass());
        StringBuilder names = new StringBuilder(), values = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            if (isNull(kv.getValue(), entity)) {
                continue;
            }
            if (i++ != 0) {
                names.append(',');
                values.append(',');
            }
            
            names.append(getLeftIdentifierQuote()).append(kv.getKey()).append(getRightIdentifierQuote());
            values.append("#{").append(kv.getValue().getName()).append('}');
        }
        
        return new SQL().INSERT_INTO(getTableName(meta, entity)).VALUES(names.toString(), values.toString()).toString();
    }
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#update(Object)
     * @param entity:实体对象
     */
    public String update(Object entity) {
        Class<?> clazz = entity.getClass();
        EntityMeta meta = EntityMeta.getPersistenceEntityMeta(clazz);
        StringBuilder setting = new StringBuilder(32);
        int i = 0;
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            if (isNull(kv.getValue(), entity) || kv.getKey().equals(meta.getIdColumnName())) {
                continue;
            }
            
            if (i++ != 0) {
                setting.append(',');
            }
            
            setting.append(getLeftIdentifierQuote()).append(kv.getKey()).append(getRightIdentifierQuote()).append("=#{").append(kv.getValue().getName()).append('}');
        }
        return new SQL().UPDATE(getTableName(meta, entity)).SET(setting.toString()).WHERE(meta.getIdColumnName() + "=#{" + meta.columnNameToFieldName(meta.getIdColumnName()) + "}").toString();
    }
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#getById(Serializable)
     */
    public String getById(final Map<String, Object> parameter) {
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        EntityMeta meta = EntityMeta.getPersistenceEntityMeta(clazz);
        return new SQL().SELECT("*").FROM(meta.getTableName()).WHERE(meta.getIdColumnName() + "=#{" + PARAM_KEY + '}').toString();
    }
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#listByEntity(Object)
     */
    @SuppressWarnings("Duplicates")
    public String listByEntity(final Object obj) {
        Class<?> clazz = obj.getClass();
        EntityMeta meta = EntityMeta.getPersistenceEntityMeta(clazz);
        StringBuilder where = new StringBuilder();
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            if (isNull(kv.getValue(), obj)) {
                continue;
            }
            where.append(getLeftIdentifierQuote()).append(kv.getKey()).append(getRightIdentifierQuote()).append("=#{").append(kv.getValue().getName()).append("} AND ");
        }
        int index = where.lastIndexOf(" AND");
        if (index > 0) {
            where.setLength(index);
        }

        return new SQL().SELECT("*").FROM(getTableName(meta, obj)).WHERE(where.toString()).toString();
    }
    
    /**
     * @see ICurdMapper#listAll()
     */
    public String listAll(final Class<?> clazz) {
        return new SQL().SELECT("*").FROM(EntityMeta.getPersistenceEntityMeta(clazz).getTableName()).toString();
    }
    
    /**
     * @see ICurdMapper#countAll()
     */
    public String countAll(final Class<?> clazz) {
        return new SQL().SELECT("count(*)").FROM(EntityMeta.getPersistenceEntityMeta(clazz).getTableName()).toString();
    }
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#deleteByEntity(Object)
     */
    public String deleteByEntity(final Object obj) {
        final EntityMeta meta = EntityMeta.getPersistenceEntityMeta(obj.getClass());
        return new SQL().DELETE_FROM(getTableName(meta, obj)).WHERE(meta.getIdColumnName() + "=#{" + meta.columnNameToFieldName(meta.getIdColumnName()) + "}").toString();
    }
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#deleteById(Serializable)
     */
    public String deleteById(final Map<String, Object> parameter) {
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        EntityMeta meta = EntityMeta.getPersistenceEntityMeta(clazz);
        return new SQL().DELETE_FROM(meta.getTableName()).WHERE(meta.getIdColumnName() + "=#{" + PARAM_KEY + '}').toString();
    }
    
    /**
     * @see com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper#deleteByIds(List)
     * @throws SQLException
     */
    public String deleteByIds(final Map<String, Object> parameter) throws SQLException {
        List ids = (List) parameter.get(PARAM_KEY);
        if (null == ids || ids.isEmpty()) {
            throw new SQLException(PARAM_KEY + " is null or empty");
        }
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        EntityMeta meta = EntityMeta.getPersistenceEntityMeta(clazz);
        String where = meta.getIdColumnName() + " in (" + concatList(ids, ",") + ')';
        return new SQL().DELETE_FROM(meta.getTableName()).WHERE(where).toString();
    }
    
    /**
     * @see ICurdMapper#truncate()
     */
    public String truncate(final Class<?> clazz) {
        return "TRUNCATE TABLE " + EntityMeta.getPersistenceEntityMeta(clazz).getTableName();
    }

    private String getTableName(EntityMeta meta, Object obj) {
        return meta.getTableName();
    }

    private boolean isNull(Field field, Object obj) {
        try {
            return field.get(obj) == null;
        } catch (IllegalAccessException e) {
            return true;
        }
    }

    private char getLeftIdentifierQuote() {
        return DBConfig.getInstance().getDbType().getLeftIdentifierQuote();
    }

    private char getRightIdentifierQuote() {
        return DBConfig.getInstance().getDbType().getRightIdentifierQuote();
    }

    private String concatList(List<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.size() - 1; i++) {
            sb.append("#{");
            sb.append(PARAM_KEY);
            sb.append("[");
            sb.append(i);
            sb.append("]}");
            sb.append(separator);
        }
        sb.append("#{");
        sb.append(PARAM_KEY);
        sb.append("[");
        sb.append(objects.size() - 1);
        sb.append("]}");
        return sb.toString();
    }
}
