package com.github.cafune1853.mybatis.support.test

import com.github.cafune1853.mybatis.support.pagination.Page
import com.github.cafune1853.mybatis.support.test.constant.SexEnum
import com.github.cafune1853.mybatis.support.test.entity.AuthorDO
import com.github.cafune1853.mybatis.support.test.mapper.AuthorMapper
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("mysql")
@ContextConfiguration(classes = Config.class)
class AuthorMapperTest extends BaseTest {
    private AuthorDO authorDO = new AuthorDO()
    @Autowired
    private AuthorMapper authorMapper

    void setup() {
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

    def "AuthorMapper#insert"() {
        def insertResult = authorMapper.insert(authorDO)
        expect:
        insertResult == 1
    }

    def "AuthorMapper#insertAndSetObjectId"() {
        authorMapper.insertAndSetObjectId(authorDO)
        expect:
        authorDO.getId() != null
    }

    def "AuthorMapper#update #getById"() {
        authorMapper.insertAndSetObjectId(authorDO)
        authorDO.setName("KK")
        authorMapper.update(authorDO)
        def updatedAuthorDO = authorMapper.getById(authorDO.getId())
        expect:
        updatedAuthorDO.getName() == "KK"
    }

    def "AuthorMapper#listByEntity"() {
        doubleInsert()
        def result = authorMapper.listByEntity(authorDO)
        expect:
        result.size() == 2
    }

    def "AuthorMapper#listByEntity == with all field null"() {
        doubleInsert()
        when:
        def res = authorMapper.listByEntity(new AuthorDO())
        then:
        res.isEmpty()
    }

    def "AuthorMapper#listAll"() {
        doubleInsert()
        def result = authorMapper.listAll()
        expect:
        result.size() == 2
    }

    def "AuthorMapper#countAll"() {
        doubleInsert()
        def count = authorMapper.countAll()
        expect:
        count == 2
    }

    def "AuthorMapper#deleteByEntity"() {
        doubleInsert()
        authorMapper.deleteByEntity(authorDO)
        def count = authorMapper.countAll()
        expect:
        count == 0
    }

    def "AuthorMapper#deleteByEntity == with all field null"() {
        doubleInsert()
        def res = authorMapper.deleteByEntity(new AuthorDO())

        expect:
        res == 0
    }

    def "AuthorMapper#deleteById"() {
        authorMapper.insertAndSetObjectId(authorDO)
        authorMapper.deleteById(authorDO.getId())
        def count = authorMapper.countAll()
        expect:
        count == 0
    }

    def "AuthorMapper#deleteByIds"() {
        doubleInsert()
        authorMapper.insertAndSetObjectId(authorDO)
        authorMapper.deleteByIds(Arrays.asList(authorDO.getId()))
        def count = authorMapper.countAll()
        expect:
        count == 2
    }

    def "AuthorMapper#listByPage"() {
        doubleInsert()
        Page page = new Page(1, 1);
        def authors = authorMapper.listByPage(page);

        expect:
        authors.size() == 1
        !authors.get(0).getSigned()
    }

    private void doubleInsert() {
        authorMapper.insert(authorDO)
        authorMapper.insert(authorDO)
    }
}