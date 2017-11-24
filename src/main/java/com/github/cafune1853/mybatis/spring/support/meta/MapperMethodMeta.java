package com.github.cafune1853.mybatis.spring.support.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;

import com.github.cafune1853.mybatis.spring.support.meta.EntityMeta;
import com.github.cafune1853.mybatis.spring.support.meta.EntityMetaFactory;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;

import com.github.cafune1853.mybatis.spring.support.annotation.AppendEntityClass;
import com.github.cafune1853.mybatis.spring.support.annotation.AutoResultMap;
import com.github.cafune1853.mybatis.spring.support.annotation.SetKeyPropertiesAndColumns;
import com.github.cafune1853.mybatis.spring.support.constant.MetaObjectShared;
import com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper;

import lombok.Getter;
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
