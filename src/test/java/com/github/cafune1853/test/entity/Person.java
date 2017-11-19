package com.github.cafune1853.test.entity;

import com.github.cafune1853.test.SexEnum;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "person")
public class Person {
	@Id
	private long id;
	private String name;
	private int age;
	private SexEnum sexEnum;
}
