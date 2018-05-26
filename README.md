## 使用文档

### 主要功能
* 支持分页，支持提供基础的Curd模板,减少Mapper类的编写工作，支持一些handler用于特殊类型的映射处理
* 目前仅支持MySQL、PostgreSQL、ORACLE以及SQLServer四种数据库

### 使用示例
#### Interceptors使用
* CurdPaginationInterceptor(提供CurdMapper模板的支持以及分页支持)

    引入配置
    ```xml
    <plugin interceptor="com.github.cafune1853.mybatis.spring.support.interceptor.CurdPaginationInterceptor">
      <!-- 配置方言，不指定则默认为mysql,可选参数为mysql|oracle|sqlserver|postgresql -->
      <property name="dialect" value="mysql"/> 
      <!-- 是否手工更新gmtModified字段,默认为false,即采取数据库自动更新的方式(ON UPDATE/Trigger); 如果设置为true,且
           update操作传入的BaseEntity#gmtModified非空,那么会更新该字段到数据库,推荐设为false,使用数据库自动更新的方式 -->
      <property name="manualUpdateGmtModified">false</property>
    </plugin>
    ```
    使用Curd模板
    ```java
    //泛型参数为该Mapper对应的实体类
     public interface MyMapper extends com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper<MyEntity>{
	
     }
    ```
    使用分页，只要在参数中传递Page方法即可
    ```java
      public interface MyMapper extends com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper<MyEntity>{
	    @org.apache.ibatis.annotations.Select("select * from my_entity")
        List<MyEntity> pagination(Page page);
      }
    ```
#### Handlers使用
* FieldEnumTypeHandler(使用enum类的一个域，来代表这个enum类映射到数据库)
    
    配置handler
    ```xml
       <typeHandlers>
              <!-- 注意，在这里一定要指定要映射的具体Enum -->
              <typeHandler handler="com.github.cafune1853.mybatis.spring.support.handler.FieldEnumTypeHandler"
                           javaType="com.github.cafune1853.test.SexEnum"/>
       </typeHandlers>
    ```
    在具体的Enum类中，使用EnumRepresentField标识用来代表该Enum的域
    ```java
      public enum SexEnum {
          MALE(1), FEMALE(2);
          //目前可以用来代表Enum类的字段类型有short/Short/int/Integer/long/Long/String
          //如果注解在其他域上则会报错。
          @EnumRepresentField
          private int type;
      
          SexEnum(int type) {
              this.type = type;
          }
      }
    ```
    
### 简易原理
CurdPaginationInterceptor: Dao.proxyMethod -> Executor proxy -> ProviderSqlSource.