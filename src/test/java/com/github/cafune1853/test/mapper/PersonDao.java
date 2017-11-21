package com.github.cafune1853.test.mapper;

import com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.github.cafune1853.test.entity.Person;

public interface PersonDao extends ICurdMapper<Person> {
    @Insert("INSERT INTO `person`(`name`, `age`, `sex_enum`) VALUES(#{person.name}, #{person.age}, #{person.sexEnum})")
    long add(@Param("person") Person person);
}
