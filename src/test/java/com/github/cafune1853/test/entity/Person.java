package com.github.cafune1853.test.entity;

import javax.persistence.Id;
import javax.persistence.Table;

import com.github.cafune1853.test.SexEnum;

import lombok.Data;

@Data
@Table(name = "person")
public class Person {
    @Id
    private long idx;
    private String name;
    private int age;
    private SexEnum sexEnum;
}
