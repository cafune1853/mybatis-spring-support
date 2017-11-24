package com.github.cafune1853.mybatis.support.test.entity;

import com.github.cafune1853.mybatis.support.entity.BaseLongIdEntity;
import com.github.cafune1853.mybatis.support.test.constant.SexEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "author")
public class AuthorDO extends BaseLongIdEntity{
	@Column(name = "name")
	private String name;
	@Column(name = "age")
	private Integer age;
	// 性别
	@Column(name = "sex")
	private SexEnum sex;
	// 是否签约
	@Column(name = "is_signed")
	private Boolean signed;
}
