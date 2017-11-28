package com.github.cafune1853.mybatis.support.test.entity;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.cafune1853.mybatis.support.entity.BaseLongIdEntity;
import lombok.Data;

@Data
@Table(name = "blog")
public class BlogDO extends BaseLongIdEntity{
	@Column(name = "title")
	private String title;
	@Column(name = "content")
	private String content;
	@Column(name = "author_id")
	private Long authorId;
}
