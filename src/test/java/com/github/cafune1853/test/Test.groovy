package com.github.cafune1853.test

import com.github.cafune1853.mybatis.spring.support.pagination.Page
import com.github.cafune1853.test.entity.Person
import com.github.cafune1853.test.mapper.PersonDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = Config.class)
class Test extends Specification {
    @Autowired
    private PersonDao personDao

    def "PersonDao#add"() {
        Person person = new Person()
        person.setName("doggy")
        person.setAge(23)
        person.setSexEnum(SexEnum.MALE)
        def res = personDao.insertAndSetObjectId(person)
        println res

        expect:
        res != 0
    }

    def "PersonDao#pagination"(){
        def res = personDao.pagination(new Page(1, 2))
        expect:
        res.size() == 2
    }

    def "PersonDao#getById"() {
        Person person = personDao.getById(7)
        println person
        expect:
        true
    }

    def "PersonDao#deleteByIds"(){
        def res = personDao.deleteByIds(Arrays.asList(37, 38))
        expect:
        res == 2
    }
}