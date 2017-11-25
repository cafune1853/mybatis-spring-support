import spock.lang.Specification

/**
 * @author doggy
 * Created on 2017-11-25.
 */
public class Test extends Specification{
    List<String> list = new ArrayList<>()
    def "testConcurrentModificationException"(){
        list.add("s1")
        list.add("s2")
        list.add("s3")
        when:
            for (String s:list){
                list.remove("s2")
                println s
            }

        then:
            thrown(ConcurrentModificationException)
    }
}
