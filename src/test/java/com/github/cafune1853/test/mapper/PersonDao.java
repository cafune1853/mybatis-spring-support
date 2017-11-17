package com.github.cafune1853.test.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.github.cafune1853.test.entity.Person;

public interface PersonDao {
    @Insert("INSERT INTO `person`(`name`, `age`) VALUES(#{person.nameC}, #{person.age})")
    long add(@Param("person") Person person);

    @Select("SELECT * FROM `person` WHERE `id` = #{id}")
    Person getById(@Param("id") long id);
}
