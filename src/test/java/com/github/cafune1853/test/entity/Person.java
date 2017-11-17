package com.github.cafune1853.test.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "person")
public class Person {
	@Id
	private long id;
	private String nameC;
	private int age;
}
