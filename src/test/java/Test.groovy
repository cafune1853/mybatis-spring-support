import com.github.cafune1853.mybatis.spring.support.mapper.TestMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


@ContextConfiguration(classes = Config.class)
class Test extends Specification{
    @Autowired
    private TestMapper testMapper

    def testTest(){
        def x = testMapper.test()
        expect:
        x == "test"
    }
}