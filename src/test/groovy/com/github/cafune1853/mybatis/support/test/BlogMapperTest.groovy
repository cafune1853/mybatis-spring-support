package com.github.cafune1853.mybatis.support.test

import com.github.cafune1853.mybatis.support.test.constant.SexEnum
import com.github.cafune1853.mybatis.support.test.entity.AuthorDO
import com.github.cafune1853.mybatis.support.test.entity.BlogDO
import com.github.cafune1853.mybatis.support.test.mapper.AuthorMapper
import com.github.cafune1853.mybatis.support.test.mapper.BlogMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("mysql")
@ContextConfiguration(classes = Config.class)
class BlogMapperTest extends BaseTest {
    @Autowired
    private BlogMapper blogMapper
    @Autowired
    private AuthorMapper authorMapper
    private BlogDO blogDO = new BlogDO()
    private AuthorDO authorDO = new AuthorDO()

    @Override
    void setup() {
        blogDO.setTitle("title")
        blogDO.setContent("Doggy with doggy")
        authorDO.setName("YW")
        authorDO.setAge(16)
        authorDO.setSigned(false)
        authorDO.setSex(SexEnum.FEMALE)
    }


    def "testGetByBlogId"() {
        authorMapper.insertAndSetObjectId(authorDO)
        blogDO.setAuthorId(authorDO.getId())
        blogMapper.insertAndSetObjectId(blogDO)
        def ret = blogMapper.getByBlogId(blogDO.getId())
        expect:
        ret != null
    }
}
