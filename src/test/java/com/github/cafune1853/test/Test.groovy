package com.github.cafune1853.test

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
        def res = personDao.insert(person)
        println res

        expect:
        res != 0
    }

    def "PersonDao#getById"(){
        Person person = personDao.getById(7)
        println person
        expect:
        true
    }
}