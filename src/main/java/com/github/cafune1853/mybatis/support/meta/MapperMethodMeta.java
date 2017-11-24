package com.github.cafune1853.mybatis.support.meta;

import java.util.Collections;
import java.util.List;

import com.github.cafune1853.mybatis.support.annotation.AutoResultMap;
import org.apache.ibatis.mapping.ResultMap;

import com.github.cafune1853.mybatis.support.annotation.AppendEntityClass;

import lombok.extern.slf4j.Slf4j;

/**
 * 用于解析Mapper方法的元数据信息。
 * @author huangzhw
 */
@Slf4j
public final class MapperMethodMeta {
    /** 该Mapper方法对应的实体类 */
    private final Class<?> entityClazz;
    /** 是否追加实体类作为Provider的额外参数, 见{@link AppendEntityClass}*/
    private final boolean appendClazzAsArg;
    /** 是否自动在MappedStatement中设置结果集,见{@link AutoResultMap}*/
    private final boolean autoResultMap;
    /** ResultMap列表，使用entityClazz生成 */
    private final List<ResultMap> resultMaps;
    /** 是否往MappedStatement中设置keyProperties和keyColumns */
    private final boolean setKeyPropertiesAndColumns;
    private final String keyProperty;
    private final String keyColumn;

    MapperMethodMeta(Class<?> entityClazz, boolean appendClazzAsArg, boolean autoResultMap, List<ResultMap> resultMaps, boolean setKeyPropertiesAndColumns, String keyProperty, String keyColumn) {
        this.entityClazz = entityClazz;
        this.appendClazzAsArg = appendClazzAsArg;
        this.autoResultMap = autoResultMap;
        this.resultMaps = Collections.unmodifiableList(resultMaps);
        this.setKeyPropertiesAndColumns = setKeyPropertiesAndColumns;
        this.keyProperty = keyProperty;
        this.keyColumn = keyColumn;
    }

    public Class<?> getEntityClazz() {
        return entityClazz;
    }

    public boolean isAppendClazzAsArg() {
        return appendClazzAsArg;
    }

    public boolean isAutoResultMap() {
        return autoResultMap;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public boolean isSetKeyPropertiesAndColumns() {
        return setKeyPropertiesAndColumns;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public String getKeyColumn() {
        return keyColumn;
    }
}
