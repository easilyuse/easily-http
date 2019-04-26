### easily-http

easily-http是一款基于注解的接口式java http 客户端api类库，仅需少量的代码，便可以实现http api客户端

### easily-http支持的jdk版本

该类库支持jdk8+

### easily-http在项目中如何引用

在项目中的pom中引入

```
 <dependency>
      <groupId>com.github.easilyuse</groupId>
      <artifactId>easily-http</artifactId>
      <version>1.0.0.RELEASE</version>
</dependency>
```

如果引入项目中，报
SLF4J: Class path contains multiple SLF4J bindings.

则引入为


```
 <dependency>
      <groupId>com.github.easilyuse</groupId>
      <artifactId>easily-http</artifactId>
      <version>1.0.0.RELEASE</version>
    <exclusions>
      <exclusion>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
      </exclusion>
    </exclusions>
    </dependency>
```

如果项目中，有使用cglib，如果项目中有使用spring，且easily-http的接口注入失败。则引入改为


```
 <dependency>
      <groupId>com.github.easilyuse</groupId>
      <artifactId>easily-http</artifactId>
      <version>1.0.0.RELEASE</version>
    <exclusions>
        <exclusion>
          <groupId>cglib</groupId>
          <artifactId>cglib</artifactId>
        </exclusion>
    </exclusions>
    </dependency>
```

如果上述的问题在项目中都存在，则引入改为

```
 <dependency>
      <groupId>com.github.easilyuse</groupId>
      <artifactId>easily-http</artifactId>
      <version>1.0.0.RELEASE</version>
    <exclusions>
      <exclusion>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
      </exclusion>
        <exclusion>
          <groupId>cglib</groupId>
          <artifactId>cglib</artifactId>
        </exclusion>
    </exclusions>
    </dependency>
```

### easily-http基本语法


```
@HttpClient(url="http://localhost:8080")
public interface UserHttpClient {

    @HttpReqMethod(path = "/u/login",HTTP_METHOD = HttpMethod.GET)
    User login(@HttpReqBean UserDTO userDTO);


    @HttpReqMethod(path = "/u/findUserById")
    User findUserById(@HttpReqParam("id") Long id);


    @HttpReqMethod(path = "/u/saveOrUpdateUser",HTTP_METHOD = HttpMethod.POST,contentType = MimeType.APPLICATION_JSON)
    Long saveOrUpdateUser(@HttpReqBean UserDTO userDTO);


    @HttpReqMethod(path = "/u/listPage",HTTP_METHOD = HttpMethod.POST,expectReturnType = User.class)
    List<User> listPage(@HttpReqBean UserDTO userDTO, @HttpReqParam("pageNo")int pageNo, @HttpReqParam("pageSize")int pageSize);


    @HttpReqMethod(path = "/u/deleteUserById")
    boolean deleteUserById(@HttpReqParam("id") Long id);


}

```
### easily-http接口如何使用
> 1、项目中不是springboot项目，则接口使用方式为


```
    UserHttpClient  userHttpClient = HttpClientServiceUtil.getInstance().getService(UserHttpClient.class);
```

> 2、项目是springboot项目，则接口使用方式为

```
a、在springboot的启动类上加上
@EnableHttpClients(basePackages = {"你自己的需要扫描的包"})

b、使用 @Autowired进行注入
 @Autowired
private UserHttpClient userHttpClient;
```

### 
easily-http详细使用示例可以查看
[easily-http-example](https://github.com/easilyuse/easily-http-example)


### 接口注解说明

注解 | 注解目标 |用法
-------|-----|-----|
@HttpClient  | 类|用于配置http调用地址，默认为GET请求
@HttpReqMethod  | 方法 |该注解中path用用于配置http请求的相对路径；capwordsRequired用于配置参数首字母是否进行大小写；contentType 用于配置请求的内容类型，目前只支持表单和JSON；expectReturnType用于配置期待响应返回的对象类型参数，通常用于返回值是复杂对象集合，诸如List<User>，则expectReturnType可以配置为User.class
@HttpReqParam |方法参数| 用于当方法请求参数为简单参数，比如string,int等基本类型参数，该注解不能与@HttpReqListBean同时使用。注解中指定的value必须和远程http请求参数名称一致
@HttpReqBean | 方法参数| 当方法请求参数为一个对象时，可以使用该注解，该注解不能和HttpReqListParam同时使用
@HttpReqListParam | 方法参数 | 方法请求参数为集合时，可以用该注解，该注解不能和@HttpReqBean或者@HttpReqParam同时使用

 

