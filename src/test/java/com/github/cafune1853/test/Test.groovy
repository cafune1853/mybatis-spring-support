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
        person.setNameC("doggy")
        person.setAge(23)
        def res = personDao.add(person)
        println res

        expect:
        res != 0
    }

    def "PersonDao#getById"(){
        Person person = personDao.getById(27)
        println person
        expect:
        true
    }
}