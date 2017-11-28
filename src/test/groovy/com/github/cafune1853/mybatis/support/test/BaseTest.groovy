package com.github.cafune1853.mybatis.support.test

import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Specification

abstract class BaseTest extends Specification {
    @Autowired
    protected SqlSessionFactory sessionFactory
    @Shared
    protected volatile boolean dbInitialized = false

    void setup() {
        if (!dbInitialized) {
            synchronized (AuthorMapperTest.class) {
                if (!dbInitialized) {
                    initDB()
                    dbInitialized = true
                }
            }
        }
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
}
