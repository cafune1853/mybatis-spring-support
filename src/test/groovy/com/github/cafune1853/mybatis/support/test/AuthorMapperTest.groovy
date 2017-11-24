package com.github.cafune1853.mybatis.support.test

import com.github.cafune1853.mybatis.support.test.constant.SexEnum
import com.github.cafune1853.mybatis.support.test.entity.AuthorDO
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import com.github.cafune1853.mybatis.support.test.mapper.AuthorMapper

@ActiveProfiles("mysql")
@ContextConfiguration(classes = Config.class)
class AuthorMapperTest extends Specification {
    private AuthorDO authorDO = new AuthorDO()
    @Autowired
    private SqlSessionFactory sessionFactory
    @Autowired
    private AuthorMapper authorMapper
    @Shared private volatile boolean dbInitialized = false

    void setup(){
        if(!dbInitialized){
            synchronized (AuthorMapperTest.class){
                if(!dbInitialized){
                    initDB()
                    dbInitialized = true
                }
            }
        }
        authorMapper.truncate()
        authorDO.setName("Tom")
        authorDO.setAge(15)
        authorDO.setSex(SexEnum.MALE)
        authorDO.setSigned(false)
    }

    private void initDB() {
        SqlSession sqlSession = null
        Reader reader = null
        try {
            sqlSession = sessionFactory.openSession()
            ScriptRunner scriptRunner = new ScriptRunner(sqlSession.getConnection())
            reader = Resources.getResourceAsReader("db.sql")
            scriptRunner.runScript(reader)
        } finally {
            if (sqlSession != null) {
                sqlSession.close()
            }
            if (reader != null) {
                reader.close()
            }
        }
    }

    def "AuthorMapper#insert"(){
        def insertResult = authorMapper.insert(authorDO)
        expect:
        insertResult == 1
    }

    def "AuthorMapper#insertAndSetObjectId"(){
        def insertResult = authorMapper.insertAndSetObjectId(authorDO)
        expect:
        authorDO.getId() != null
    }

    def "AuthorMapper#update #getById"(){
        authorMapper.insertAndSetObjectId(authorDO)
        authorDO.setName("KK")
        authorMapper.update(authorDO)
        def updatedAuthorDO = authorMapper.getById(authorDO.getId())
        expect:
        updatedAuthorDO.getName() == "KK"
    }

    def "AuthorMapper#listByEntity"(){
        authorMapper.insert(authorDO)
        authorMapper.insert(authorDO)
        def result = authorMapper.listByEntity(authorDO)
        expect:
        result.size() == 2
    }
}