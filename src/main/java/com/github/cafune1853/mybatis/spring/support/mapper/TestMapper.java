package com.github.cafune1853.mybatis.spring.support.mapper;

import com.github.cafune1853.mybatis.spring.support.provider.CrudProvider;
import org.apache.ibatis.annotations.SelectProvider;

public interface TestMapper {
	@SelectProvider(type = CrudProvider.class, method = "test")
	String test();
}
