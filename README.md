# spring-commons
## Overview
This project contains the general-purpose tools to spring.  Project is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

-----
## Roadmap
Consult the proyect for details on the current [spring-commons roadmap]([https://github.com/damianwajser/spring-commons/projects/1](https://github.com/damianwajser/spring-commons/projects/1)).

## Get it!
### Install
#### Maven
Functionality of this package is contained in Java package `com.github.damianwajser`, and can be used using following Maven dependency:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.3.0.RELEASE</version>
</parent>
<properties>
 ...
     <!-- Use the latest version whenever possible. -->
     <spring.commons>{lastversion}</spring.commons>
 ...
</properties>

<dependencies>
 ...
    <dependency>
        <groupId>com.github.damianwajser</groupId>
        <artifactId>spring-commons</artifactId>
        <version>${spring.commons}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
 ...
</dependencies>
 ```
 #### Gradle
 ```xml
 compile 'com.github.damianwajser:spring-commons:{lastVersion}'
 ```
 ## Usage Create a spring-boot application.
```java
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.github.damianwajser","{YOUR-PACKAGE}"})
public class Application {
   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}
```
#### General configuration:
| Key | Value | Module | Reference
|--|--|--|--
|spring.jackson.property-naming-strategy|PropertyNamingStrategy.SnakeCaseStrategy | Spring web | format JSON response when Object is return in a controller
|spring.commons.app.name|"change spring.commons.app.name property"|All spring-commons| the name of microservice
## Modules
## 1 [spring-commons-exception](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-exception "spring-commons-exception")

Collection of exceptions for the most common cases when rest apis are built, and override the http code.

In addition, they request additional information for errors.

They can be used on their own, or they are caught by spring-commons-exception-handler and this information is used to generate a nice error message.

For example:
|Exception|Http Code |
|--|--|
| BadRequestException | 400
| PaymentRequiredException | 402
| ForbiddenException | 403
| PermissionDeniedException | 403
| NotFoundException | 404

## 2 [spring-commons-rest-validation](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-rest-validation "spring-commons-rest-validation")

This project override all annotations for standard JSR annotations.

Some annotations accept additional attributes like ***isNulleable***, but the message and the ***bussisnessCode*** attributes are common to all of them.

Message: This is the message that will usually be rendered when the value of the respective property fails validation.

## 3 [spring-commons-exception-handler](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-exception-handler "spring-commons-exception-handler")
This module is responsible for generating error messages (REST) when an exception occurs, generating a unique interface for these occurrences. It is also in charge of correctly setting the HTTP codes in the message.

On the other hand, the internationalization option is enabled, for which in all the exceptions found in ***spring-commons-exceptions*** or the validations found in ***spring-commons-rest-validation*** We can enter placeholders when we talk about errors.

The language is selected by the client based on the header ***Accept-Lenguage: $ {locale}*** and this module will take it from the corresponding message.properties.

## 4 [spring-commons-http-fixer](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-http-fixer "spring-commons-http-fixer")
This project is in charge of solving the most common problems related to http codes in spring-mvc REST responses.
Among other things, set the default http 201 code in the response when we are using the POST verb.

## 5 [spring-commons-resttemplate-interceptor](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-resttemplate-interceptor "spring-commons-resttemplate-interceptor")

This project registers a RestTemplate to be used by the application.

At the same time, it incorporates an Interceptor to add custom headers (those that start with "X-"), to generate traceability between the microservices.

## 6 [spring-commons-logger](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-logger "spring-commons-logger")
This module generates useful tools for logging.

Among its features we find:
- StatsFilter (generates a log with the duration for each request)
- MDCFilter (it incorporates parameters in the MDC to be used in the loggin.pattern)
- RequestIdGenerator (a UUID is generated for each request in order to have traceability)

## 7 [spring-commons-logger-logstash](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-logger-logstash "spring-commons-logger-logstash")

This module configures the connector to logstash async way.

## 8 [spring-commons-cache](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-idempotency "spring-commons-cache")

This module tries to solve the typical problems that we encounter when we use Redis as Cache in spring.
Configure:
- CacheManager
- RedisTemplate

It also improves the lifetime of the keys when we use @Cacheable.

## 9 [spring-commons-idempotency](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-idempotency "spring-commons-idempotency")
This module tries to solve the problems associated with idempotence. For them, create a filter within the spring chain of responsibilities. When the first request is made, it saves in redis the request sent by the client associated with an idempotence key. When another request is made two things can happen:
 1. The first request finished executing, which returns the same response that was obtained in the first call.
 2. In case the first request is still running, a message will be returned indicating the conflict.

This configuration is done by registering some beans and properties, you can see the following example:

#### Properties
| Key | Posible Value | Reference | Default Value
|--|--|--|--
spring.commons.idempotency.enabled | true/false | Enable the module | false
spring.commons.idempotency.message | Any String | |
spring.commons.idempotency.ttl | Any nunmber ||
spring.commons.idempotency.badrequest.code| Any String | | 400
spring.commons.idempotency.conflict.code| Any String | | 409
spring.commons.idempotency.conflict.mesasge| Any String | |idempotency key is bussy
```java
@Configuration
public class IdempotencyConfiguration {

  @Bean
  public IdempotencyEndpoints idempotencyEndpoints() {
    IdempotencyEndpoints idempotencyEndpoints = new IdempotencyEndpoints();
    // register endpoint by all Http Methods and generic Key generator
    // (The idempotence key is generated based on the header sent by the client, X-Idempotency-Key)
    idempotencyEndpoints.addIdempotencyEndpoint("/idempotency_generic");
    //Customize another enpoint only by POST method and custom keyGenerator
    idempotencyEndpoints.addIdempotencyEndpoint("/idempotency_by_custom", new FooIdempotencyKeyGenerator(), HttpMethod.POST);
    return idempotencyEndpoints;
  }
}
```
Eneable the module
```properties
spring.commons.idempotency.enabled=true
```

Remember that this module works with cache, with which you should first configure your ***spring-commons-cache***, for which I leave you an example:

1. Suppose we have the following domain object
```java
public class FooObject {

   private String value;
   //The Empty Constructor is required
   public FooObject(){ }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
```
2. So we want some properties of the object to be part of the idempotence key, for which we should create our own KeyGenerator  and override the "generateKey" method. The declaration of the generics is important, since the request will be stopped and a mapping will be made towards the declared object, it can return InternalErrorOfServer in case of a ClassCastException.

```java
                                                                           //very important
									  //generic
public class FooIdempotencyKeyGenerator<T> implements IdempotencyKeyGenerator<FooObject> {

   private static final String IDEMPOTENCY_DEFALUT_HEADER = "X-Idempotency-Key";

   @Override
   public String generateKey(HttpHeaders headers, HttpMethod method, String path, FooObject request) {
      String key = getHeaderValue(headers, IDEMPOTENCY_DEFALUT_HEADER);
      return path + "-" + key + "-" + method.toString() + "-" + request.getValue();
   }

   protected String getHeaderValue(HttpHeaders headers, String headerKey) {
      List<String> idempotencyHeader = headers.get(headerKey);
      String key;
      if (idempotencyHeader != null) {
         key = idempotencyHeader.stream().collect(Collectors.joining("-"));
      } else {
         // ArgumentNotFoundException is used to return a bad request indicating that the field is mandatory
         throw new ArgumentNotFoundException(headerKey);
      }
      return key;
   }
}
```
## 9 [spring-commons-actuator](https://github.com/damianwajser/spring-commons/tree/master/spring-commons-actuator "spring-commons-actuator")

````xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
    <version>4.0.0</version>
    <executions>
        <execution>
            <id>get-the-git-info</id>
            <goals>
                <goal>revision</goal>
            </goals>
            <phase>initialize</phase>
        </execution>
    </executions>
    <configuration>
        <generateGitPropertiesFile>true</generateGitPropertiesFile>
        <generateGitPropertiesFilename>${project.build.outputDirectory}/git.properties</generateGitPropertiesFilename>
        <commitIdGenerationMode>full</commitIdGenerationMode>
        <offline>true</offline>
    </configuration>
</plugin>
````

## License
The Spring Framework is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
