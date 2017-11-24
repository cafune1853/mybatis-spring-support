package com.github.cafune1853.mybatis.support.test.mapper;

import com.github.cafune1853.mybatis.support.mapper.ICurdMapper;
import com.github.cafune1853.mybatis.support.pagination.Page;
import com.github.cafune1853.mybatis.support.test.entity.AuthorDO;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AuthorMapper extends ICurdMapper<AuthorDO> {
	@Results({@Result(column = "is_signed", property = "signed")})
	@Select("select * from `author`")
	List<AuthorDO> listByPage(Page page);
}
