package com.github.cafune1853.mybatis.support.provider;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.github.cafune1853.mybatis.support.config.DBConfig;
import com.github.cafune1853.mybatis.support.mapper.ICurdMapper;
import com.github.cafune1853.mybatis.support.meta.EntityMeta;
import com.github.cafune1853.mybatis.support.meta.EntityMetaFactory;

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
     * @see ICurdMapper#insert(Object)
     * @see ICurdMapper#insertAndSetObjectId(Object)
     * @param entity: 实体对象
     */
    public String insert(Object entity) {
        EntityMeta meta = EntityMetaFactory.getEntityMeta(entity.getClass());
        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            boolean isNeedToFilter = isNull(kv.getValue(), entity) || isNeedToFilterOnInsert(kv.getKey());
            if (isNeedToFilter) {
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
     * @see ICurdMapper#update(Object)
     * @param entity:实体对象
     */
    public String update(Object entity) {
        Class<?> clazz = entity.getClass();
        EntityMeta meta = EntityMetaFactory.getEntityMeta(clazz);
        StringBuilder setting = new StringBuilder(32);
        int i = 0;
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            boolean isNeedToFilter = isNull(kv.getValue(), entity) || kv.getKey().equals(meta.getIdColumnName()) || isNeedToFilterOnUpdate(kv.getKey());
            if (isNeedToFilter) {
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
     * @see ICurdMapper#getById(Serializable)
     */
    public String getById(final Map<String, Object> parameter) {
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        EntityMeta meta = EntityMetaFactory.getEntityMeta(clazz);
        return new SQL().SELECT("*").FROM(meta.getTableName()).WHERE(meta.getIdColumnName() + "=#{" + PARAM_KEY + '}').toString();
    }

    /**
     * @see ICurdMapper#listByEntity(Object)
     */
    @SuppressWarnings("Duplicates")
    public String listByEntity(final Object entity) {
        Class<?> clazz = entity.getClass();
        EntityMeta meta = EntityMetaFactory.getEntityMeta(clazz);
        StringBuilder where = new StringBuilder();
        for (Map.Entry<String, Field> kv : meta.getColumnFieldMaps().entrySet()) {
            boolean isNeedToFilter = isNull(kv.getValue(), entity) || isNeedToFilterOnSearchCondition(kv.getKey());
            if (isNeedToFilter) {
                continue;
            }
            where.append(getLeftIdentifierQuote()).append(kv.getKey()).append(getRightIdentifierQuote()).append("=#{").append(kv.getValue().getName()).append("} AND ");
        }
        int index = where.lastIndexOf(" AND");
        if (index <= 0) {
            //在所有域为空的情况下，相当于没有查询到数据
            return new SQL().SELECT("1").FROM(getTableName(meta, entity)).WHERE("false").toString();
        } else {
            where.setLength(index);
        }

        return new SQL().SELECT("*").FROM(getTableName(meta, entity)).WHERE(where.toString()).toString();
    }

    /**
     * @see ICurdMapper#listAll()
     */
    public String listAll(final Class<?> clazz) {
        return new SQL().SELECT("*").FROM(EntityMetaFactory.getEntityMeta(clazz).getTableName()).toString();
    }

    /**
     * @see ICurdMapper#countAll()
     */
    public String countAll(final Class<?> clazz) {
        return new SQL().SELECT("count(*)").FROM(EntityMetaFactory.getEntityMeta(clazz).getTableName()).toString();
    }

    /**
     * @see ICurdMapper#deleteByEntity(Object)
     */
    public String deleteByEntity(final Object entity) {
        final EntityMeta meta = EntityMetaFactory.getEntityMeta(entity.getClass());
        StringBuilder equalConditionBuilder = new StringBuilder();
        meta.getColumnFieldMaps().forEach((c, f) -> {
            boolean isNeedToFilter = isNull(f, entity) || isNeedToFilterOnSearchCondition(c);
            if (isNeedToFilter) {
                return;
            }
            equalConditionBuilder.append(getLeftIdentifierQuote());
            equalConditionBuilder.append(c);
            equalConditionBuilder.append(getRightIdentifierQuote());
            equalConditionBuilder.append("=#{");
            equalConditionBuilder.append(f.getName());
            equalConditionBuilder.append("}");
            equalConditionBuilder.append(" and ");
        });
        if (equalConditionBuilder.length() == 0) {
            // 无非空域，则认为不影响任何列
            return new SQL().DELETE_FROM(getTableName(meta, entity)).WHERE("false").toString();
        } else {
            equalConditionBuilder.setLength(equalConditionBuilder.length() - 5);
        }
        return new SQL().DELETE_FROM(getTableName(meta, entity)).WHERE(equalConditionBuilder.toString()).toString();
    }

    /**
     * @see ICurdMapper#deleteById(Serializable)
     */
    public String deleteById(final Map<String, Object> parameter) {
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        EntityMeta meta = EntityMetaFactory.getEntityMeta(clazz);
        return new SQL().DELETE_FROM(meta.getTableName()).WHERE(meta.getIdColumnName() + "=#{" + PARAM_KEY + '}').toString();
    }

    /**
     * @see ICurdMapper#deleteByIds(List)
     * @throws SQLException
     */
    public String deleteByIds(final Map<String, Object> parameter) throws SQLException {
        List ids = (List) parameter.get(PARAM_KEY);
        if (null == ids || ids.isEmpty()) {
            throw new SQLException(PARAM_KEY + " is null or empty");
        }
        Class<?> clazz = (Class<?>) parameter.get(CLASS_KEY);
        EntityMeta meta = EntityMetaFactory.getEntityMeta(clazz);
        String where = meta.getIdColumnName() + " in (" + handleIdList(ids, PARAM_KEY) + ')';
        return new SQL().DELETE_FROM(meta.getTableName()).WHERE(where).toString();
    }

    /**
     * @see ICurdMapper#truncate()
     */
    public String truncate(final Class<?> clazz) {
        return "TRUNCATE TABLE " + EntityMetaFactory.getEntityMeta(clazz).getTableName();
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

    private boolean isNeedToFilterOnUpdate(String columnName) {
        if (SqlProviderConstants.GMT_CREATE_COLUMN_NAME.equals(columnName)) {
            return true;
        }
        if (SqlProviderConstants.GMT_MODIFIED_COLUMN_NAME.equals(columnName)) {
            if (!DBConfig.getInstance().isManualUpdateGmtModified()) {
                return true;
            }
        }
        return false;
    }

    private boolean isNeedToFilterOnInsert(String columnName) {
        return SqlProviderConstants.GMT_CREATE_COLUMN_NAME.equals(columnName) || SqlProviderConstants.GMT_MODIFIED_COLUMN_NAME.equals(columnName);
    }

    private boolean isNeedToFilterOnSearchCondition(String columnName) {
        return SqlProviderConstants.GMT_CREATE_COLUMN_NAME.equals(columnName) || SqlProviderConstants.GMT_MODIFIED_COLUMN_NAME.equals(columnName);
    }
    
    /**
     * 将id列表快速转换成字符串形式，对于基本数值类型Long/Integer以及String类型进行快速处理，避免还需要通过PrepareStatement进行参数处理。
     */
    private static String handleIdList(List<?> objects, String paramName) {
        char separator = ',';
        StringBuilder sb = new StringBuilder();
        if (objects.get(0) instanceof Long || objects.get(0) instanceof Integer) {
            for (int i = 0; i < objects.size() - 1; i++) {
                sb.append(objects.get(i));
                sb.append(separator);
            }
            sb.append(objects.get(objects.size() - 1));
        } else if (objects.get(0) instanceof String) {
            for (int i = 0; i < objects.size() - 1; i++) {
                sb.append('\'');
                sb.append(objects.get(i));
                sb.append('\'');
                sb.append(separator);
            }
            sb.append('\'');
            sb.append(objects.get(objects.size() - 1));
            sb.append('\'');
        } else {
            for (int i = 0; i < objects.size() - 1; i++) {
                sb.append("#{");
                sb.append(paramName);
                sb.append("[");
                sb.append(i);
                sb.append("]}");
                sb.append(separator);
            }
            sb.append("#{");
            sb.append(paramName);
            sb.append("[");
            sb.append(objects.size() - 1);
            sb.append("]}");
        }
        return sb.toString();
    }
}
