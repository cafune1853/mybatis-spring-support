package com.github.cafune1853.mybatis.spring.support.mapper;

import com.github.cafune1853.mybatis.spring.support.annotation.AppendEntityClass;
import com.github.cafune1853.mybatis.spring.support.annotation.AutoResultMap;
import com.github.cafune1853.mybatis.spring.support.provider.BaseProvider;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

public interface IBaseMapper<T> {
	/**
	 * 查找所有记录
	 */
	@SelectProvider(type = BaseProvider.class, method = "findAll")
	@AutoResultMap
	List<T> findAll();
	
	/**
	 * 查询（根据whereEntity条件）
	 *
	 * @param t
	 * @return
	 */
	@SelectProvider(type = BaseProvider.class, method = "findByEntity")
	List<T> findByEntity(T t);
	
	/**
	 * 统计所有记录行数
	 */
	@SelectProvider(type = BaseProvider.class, method = "countAll")
	@AppendEntityClass
	int countAll();
	
	/**
	 * 删除所有记录
	 */
	@DeleteProvider(type = BaseProvider.class, method = "deleteAll")
	@AppendEntityClass
	int deleteAll();
	
	/**
	 * 删除并创建一个同名的新表，可以回收innodb表空间，而且比deleteAll快，多用于测试case清理数据
	 */
	@DeleteProvider(type = BaseProvider.class, method = "truncate")
	@AutoResultMap
	int truncate();
	
	/**
	 * 根据主键查找记录
	 */
	@SelectProvider(type = BaseProvider.class, method = "findById")
	@AutoResultMap
	T findById(Serializable id);
	
	/**
	 * 插入记录,返回值不是id,
	 * 使用object的getId方法获得id
	 *
	 * @param t
	 * @return 影响行数
	 */
	@InsertProvider(type = BaseProvider.class, method = "insert")
	int insert(T t);
	
	/**
	 * 插入记录并设置t的id属性为数据库的自增ID,返回值是修改的记录行数
	 * 使用object的getId方法获得id
	 *
	 * @param t
	 * @return 影响行数
	 * @see ICrudMapper#insertAndSetObjectId(Object)
	 */
	@InsertProvider(type = BaseProvider.class, method = "insert")
	@Options(useGeneratedKeys = true)
	@Deprecated
	int insertAndGetId(T t);
	
	/**
	 * 插入记录并设置t的id属性为数据库的自增ID,返回值是修改的记录行数
	 * 使用object的getId方法获得id
	 *
	 * @param t
	 * @return 影响行数
	 */
	@InsertProvider(type = BaseProvider.class, method = "insert")
	@Options(useGeneratedKeys = true)
	int insertAndSetObjectId(T t);
	
	/**
	 * 更新记录
	 */
	@UpdateProvider(type = BaseProvider.class, method = "update")
	int update(T t);
	
	@UpdateProvider(type = BaseProvider.class, method = "updateAndIncrement")
	int updateAndIncrement(T t);
	
	/**
	 * 更新记录
	 */
	@UpdateProvider(type = BaseProvider.class, method = "save")
	int save(T t);
	
	/**
	 * 删除记录
	 */
	@DeleteProvider(type = BaseProvider.class, method = "delete")
	int delete(T t);
	
	/**
	 * 根据主键删除记录
	 */
	@DeleteProvider(type = BaseProvider.class, method = "deleteById")
	@AutoResultMap
	int deleteById(Serializable id);
	
	@DeleteProvider(type = BaseProvider.class, method = "deleteByIds")
	@AutoResultMap
	int deleteByIds(@Param(BaseProvider.PARAM_KEY) List<? extends Serializable> ids);
}
