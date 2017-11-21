package com.github.cafune1853.mybatis.spring.support.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.github.cafune1853.mybatis.spring.support.annotation.AppendEntityClass;
import com.github.cafune1853.mybatis.spring.support.annotation.AutoResultMap;
import com.github.cafune1853.mybatis.spring.support.annotation.SetKeyPropertiesAndColumns;
import com.github.cafune1853.mybatis.spring.support.interceptor.CurdInterceptor;
import com.github.cafune1853.mybatis.spring.support.provider.CurdProvider;

/**
 * 要继承这个类并使用其中的方法，必须先配置{@link CurdInterceptor}
 * 作为拦截器，否则会报错。
 * @author huangzhw
 */
public interface ICurdMapper<E> {
    /**
     * 插入记录,返回值不是id,
     * 使用object的getId方法获得id
     * @see CurdProvider#insert(Object)
     * @param e: 实体类
     * @return 影响行数
     */
    @InsertProvider(type = CurdProvider.class, method = "insert")
    int insert(@Param("e") E e);
    
    /**
     * 插入记录并设置t的id属性（优先为@Id,其次寻找名为id的字段）为数据库的自增ID,
     * 返回值是修改的记录行数，使用object的getId方法获得id
     * @see CurdProvider#insert(Object)
     * @param e: 实体类
     * @return 影响行数
     */
    @InsertProvider(type = CurdProvider.class, method = "insert")
    @Options(useGeneratedKeys = true)
    @SetKeyPropertiesAndColumns
    int insertAndSetObjectId(E e);
    
    /**
     * 更新记录
     * @see CurdProvider#update(Object)
     */
    @UpdateProvider(type = CurdProvider.class, method = "update")
    int update(E e);
    
    /**
     * 根据主键查找记录
     * @see CurdProvider#getById(Map)
     */
    @SelectProvider(type = CurdProvider.class, method = "getById")
    @AutoResultMap
    E getById(Serializable id);

    /**
     * 查询（根据whereEntity条件）
     * @see CurdProvider#listByEntity(Object)
     *
     * @param e: 查询实体，只查询非空字段。
     * @return 查询结果列表
     */
    @SelectProvider(type = CurdProvider.class, method = "listByEntity")
    List<E> listByEntity(E e);
    
    /**
     * 查找所有记录
     * @see CurdProvider#listAll(Class)
     */
    @AutoResultMap
    @SelectProvider(type = CurdProvider.class, method = "listAll")
    List<E> listAll();

    /**
     * 统计所有记录行数
     * @see CurdProvider#countAll(Class)
     */
    @AppendEntityClass
    @SelectProvider(type = CurdProvider.class, method = "countAll")
    int countAll();
    
    /**
     * 删除记录
     * @see CurdProvider#deleteByEntity(Object)
     */
    @DeleteProvider(type = CurdProvider.class, method = "deleteByEntity")
    int deleteByEntity(E e);
    
    /**
     * 根据主键删除记录
     * @see CurdProvider#deleteById(Map)
     */
    @AutoResultMap
    @DeleteProvider(type = CurdProvider.class, method = "deleteById")
    int deleteById(Serializable id);
    
    /**
     * 根据ids列表批量删除
     * @see CurdProvider#deleteByIds(Map)
     * @param ids: id 列表
     * @return 影响行数
     */
    @AutoResultMap
    @DeleteProvider(type = CurdProvider.class, method = "deleteByIds")
    int deleteByIds(@Param(CurdProvider.PARAM_KEY) List<? extends Serializable> ids);
    
    /**
     * 删除并创建一个同名的新表，可以回收innodb表空间，而且比deleteAll快，多用于测试case清理数据
     * @see CurdProvider#truncate(Class)
     * @return 影响行数
     */
    @AutoResultMap
    @DeleteProvider(type = CurdProvider.class, method = "truncate")
    int truncate();
}
