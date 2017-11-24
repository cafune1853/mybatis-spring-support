package com.github.cafune1853.mybatis.support.test

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import com.github.cafune1853.mybatis.support.test.entity.AuthorDO
import com.github.cafune1853.mybatis.support.test.entity.BlogDO
import spock.lang.Shared
import spock.lang.Specification

@ActiveProfiles("mysql")
@ContextConfiguration(classes = Config.class)
class Test extends Specification {
    void setup() {
        println "setup"
    }

    void cleanup() {
        println "cleanup"
    }
    @Shared BlogDO blogDO = new BlogDO()
    AuthorDO authorDO = new AuthorDO()

    def "test1"(){
        blogDO.setTitle("title")

        expect:
        blogDO.getContent() == null
    }

    def "test2"(){
        blogDO.setContent("content")

        expect:
        blogDO.getTitle() == null
    }
}