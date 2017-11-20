package com.github.cafune1853.mybatis.spring.support.provider;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.github.cafune1853.mybatis.spring.support.config.DBConfig;
import com.github.cafune1853.mybatis.spring.support.util.PersistenceEntityMeta;
import com.github.cafune1853.mybatis.spring.support.util.StringUtil;
import org.apache.ibatis.jdbc.SQL;

import lombok.extern.slf4j.Slf4j;

/**
 * @author huangzhw
 */
@Slf4j
public class BaseProvider {
    public static final String CLASS_KEY = "CLAZZ";
    public static final String PARAM_KEY = "PARAM";
    public static final String PAGE_KEY = "PAGE";
    private static final String COLUMN_KEY = "column";
    private static final String WHERE_KEY = "where";
    private static final String ORDER_KEY = "order";
    private static final String GROUP_KEY = "groupBy";

    /**
     * 查询所有记录
     *
     * @param clazz
     * @return
     */
    public String listAll(final Class<?> clazz) {
        return new SQL().SELECT("*").FROM(PersistenceEntityMeta.getPersistenceEntityMeta(clazz).getTableName()).toString();
    }

    /**
     * 查询（根据whereEntity条件）
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("Duplicates")
    public String findByEntity(final Object obj) {
        Class<?> clazz = obj.getClass();
        PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(clazz);
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
     * 统计所有记录行数
     *
     * @param clazz
     * @return
     */
    public String countAll(final Class<?> clazz) {
        return new SQL().SELECT("count(*)").FROM(PersistenceEntityMeta.getPersistenceEntityMeta(clazz).getTableName()).toString();
    }

    /**
     * 删除所有记录
     *
     * @param clazz
     * @return
     */
    public String deleteAll(final Class<?> clazz) {
        return new SQL().DELETE_FROM(PersistenceEntityMeta.getPersistenceEntityMeta(clazz).getTableName()).toString();
    }

    /**
     * 删除旧表，并新增一个同名的新表，比deleteAll要快，而且能回收innodb的表存储空间
     *
     * @param clazz
     * @return
     */
    public String truncate(final Class<?> clazz) {
        return "TRUNCATE TABLE " + PersistenceEntityMeta.getPersistenceEntityMeta(clazz).getTableName();
    }

    /**
     * 根据主键查找记录
     *
     * @param parameter
     * @return
     */
    public String findById(final Map<String, Object> parameter) {
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(clazz);
        return new SQL().SELECT("*").FROM(meta.getTableName()).WHERE(meta.getIdColumnName() + "=#{" + PARAM_KEY + '}').toString();
    }

    /**
     * 查询所有记录(分页)
     *
     * @param map
     * @return
     */
    public String findByPage(final Map<String, Object> map) {
        Class<?> clazz = (Class<?>) map.get(CLASS_KEY);
        String names = map.containsKey(COLUMN_KEY) ? map.get(COLUMN_KEY).toString() : "*";
        SQL sql = new SQL().SELECT(names).FROM(PersistenceEntityMeta.getPersistenceEntityMeta(clazz).getTableName());
        if (map.containsKey(WHERE_KEY)) {
            Object obj = map.get(WHERE_KEY);
            if (obj != null && !StringUtil.isNullOrEmpty(obj.toString())) {
                sql.WHERE((String) obj);
            }
        }
        if (map.containsKey(ORDER_KEY)) {
            Object obj = map.get(ORDER_KEY);
            if (obj != null && !StringUtil.isNullOrEmpty(obj.toString())) {
                sql.ORDER_BY((String) map.get(ORDER_KEY));
            }
        }
        if (map.containsKey(GROUP_KEY)) {
            Object obj = map.get(GROUP_KEY);
            if (obj != null && !StringUtil.isNullOrEmpty(obj.toString())) {
                sql.GROUP_BY((String) map.get(GROUP_KEY));
            }
        }
        return sql.toString();
    }

    /**
     * 按照id查找并删除某个对象
     *
     * @param obj
     * @return
     */
    public String delete(final Object obj) {
        final PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(obj.getClass());
        return new SQL().DELETE_FROM(getTableName(meta, obj)).WHERE(meta.getIdColumnName() + "=#{" + meta.columnNameToFieldName(meta.getIdColumnName()) + "}").toString();
    }

    /**
     * 根据主键删除记录
     *
     * @return
     */
    public String deleteById(final Map<String, Object> parameter) {
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(clazz);
        return new SQL().DELETE_FROM(meta.getTableName()).WHERE(meta.getIdColumnName() + "=#{" + PARAM_KEY + '}').toString();
    }

    /**
     * 根据主键集合删除记录
     *
     * @return
     */
    public String deleteByIds(final Map<String, Object> parameter) throws SQLException {
        List ids = (List) parameter.get(PARAM_KEY);
        if (null == ids || ids.isEmpty()) {
            throw new SQLException(PARAM_KEY + " is null or empty");
        }
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(clazz);
        String where = meta.getIdColumnName() + " in (" + concatList(ids, ",") + ')';
        return new SQL().DELETE_FROM(meta.getTableName()).WHERE(where).toString();
    }

    /**
     * 更新
     *
     * @param obj
     * @return
     */
    public String updateAndIncrement(Object obj) {
        //TODO
        return null;
    }

    /**
     * 更新操作
     *
     * @param obj
     * @return String
     */
    public String update(Object obj) {
        Class<?> clazz = obj.getClass();
        PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(clazz);
        StringBuilder setting = new StringBuilder(32);
        int i = 0;
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            if (isNull(kv.getValue(), obj) || kv.getKey().equals(meta.getIdColumnName())) {
                continue;
            }

            if (i++ != 0) {
                setting.append(',');
            }

            setting.append(getLeftIdentifierQuote()).append(kv.getKey()).append(getRightIdentifierQuote()).append("=#{").append(kv.getValue().getName()).append('}');
        }
        return new SQL().UPDATE(getTableName(meta, obj)).SET(setting.toString()).WHERE(meta.getIdColumnName() + "=#{" + meta.columnNameToFieldName(meta.getIdColumnName()) + "}").toString();
    }

    /**
     * 新增操作
     *
     * @param obj
     * @return String
     */

    public String insert(Object obj) {
        PersistenceEntityMeta meta = PersistenceEntityMeta.getPersistenceEntityMeta(obj.getClass());
        StringBuilder names = new StringBuilder(), values = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            if (isNull(kv.getValue(), obj)) {
                continue;
            }
            if (i++ != 0) {
                names.append(',');
                values.append(',');
            }

            names.append(getLeftIdentifierQuote()).append(kv.getKey()).append(getRightIdentifierQuote());
            values.append("#{").append(kv.getValue().getName()).append('}');
        }

        return new SQL().INSERT_INTO(getTableName(meta, obj)).VALUES(names.toString(), values.toString()).toString();
    }

    protected String getTableName(PersistenceEntityMeta meta, Object obj) {
        return meta.getTableName();
    }
    

    /**
     * 列名判空处理
     *
     * @param field
     * @return boolean
     */
    protected boolean isNull(Field field, Object obj) {
        try {
            return field.get(obj) == null;
        } catch (IllegalAccessException e) {
            return true;
        }
    }
    
    private char getLeftIdentifierQuote(){
        return DBConfig.getInstance().getDbType().getLeftIdentifierQuote();
    }
    
    private char getRightIdentifierQuote(){
        return DBConfig.getInstance().getDbType().getRightIdentifierQuote();
    }
    
    private String concatList(List<?> objects, String separator){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.size() - 1; i++) {
            sb.append(objects.get(i));
            sb.append(separator);
        }
        sb.append(objects.get(objects.size() - 1));
        return sb.toString();
    }
}
