package com.github.cafune1853.mybatis.support.test.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.github.cafune1853.mybatis.support.mapper.ICurdMapper;
import com.github.cafune1853.mybatis.support.test.entity.BlogDO;
import com.github.cafune1853.mybatis.support.test.entity.BlogWithAuthorDO;

public interface BlogMapper extends ICurdMapper<BlogDO> {
	@ResultMap(value = "all.blogWithAuthor")
	@Select("SELECT b.id id, b.title title, b.content content,a.id author_id, a.name author_name, a.age author_age, a.sex author_sex," +
		" a.is_signed author_is_signed from blog b left join author a on b.author_id = a.id where b.id = #{blogId}")
	BlogWithAuthorDO getByBlogId(@Param("blogId") Long blogId);
}
