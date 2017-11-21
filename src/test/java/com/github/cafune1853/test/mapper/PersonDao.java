package com.github.cafune1853.test.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.github.cafune1853.mybatis.spring.support.mapper.IBaseMapper;
import com.github.cafune1853.test.entity.Person;

public interface PersonDao extends IBaseMapper<Person> {
    @Insert("INSERT INTO `person`(`name`, `age`, `sex_enum`) VALUES(#{person.name}, #{person.age}, #{person.sexEnum})")
    long add(@Param("person") Person person);

    @Select("SELECT * FROM `person` WHERE `id` = #{id}")
    Person getById(@Param("id") long id);
}
