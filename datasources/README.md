# 多数据源配置

## Features

- 动态读取多数据源配置
- 动态切换数据源
- 不影响已有数据源配置
- 支持自定义数据源密码解码器
- 支持多种连接池配置（`druid`、`hikari`）



## Architecture



## Getting started

### 依赖

**jdk版本**：17

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid</artifactId>
</dependency>
<dependency>
  <groupId>com.zaxxer</groupId>
  <artifactId>HikariCP</artifactId>
</dependency>
```



### 配置

#### demo

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:test0
    username: sa
    password:

logging:
  level:
    others.mapper: debug
    org.mybatis: debug
    root: info

mybatis:
  mapper-locations: classpath:mappers/*.xml

datasources:
  enabled: true
  sources:
    db:
      is-primary: true
      url: jdbc:h2:mem:test1
      username: sa
      password:
      driver-class-name: org.h2.Driver
    db-druid:
      url: jdbc:h2:mem:test2
      username: sa
      password:
      driver-class-name: org.h2.Driver
      druid:
        initial-size: 6 # 连接池初始化大小
        min-idle: 11 # 最小空闲连接数
        max-active: 21 # 最大连接数
    db-hikari:
      url: jdbc:h2:mem:test3
      username: sa
      password:
      driver-class-name: org.h2.Driver
      hikari:
        connection-test-query: SELECT 1
        minimum-idle: 6 # 最小空闲连接数
        maximum-pool-size: 11 # 连接池的最大连接数。
        idle-timeout: 30001 # 连接的最大空闲时间
        max-lifetime: 60001 # 连接的最大生命周期
        connection-timeout: 30001 # 连接超时时间
```

#### 参数说明

##### datasources

| 属性    | 说明             |
| ------- | ---------------- |
| enabled | 是否加载多数据源 |
| sources | 数据源           |

##### datasources.sources

| 属性                 | 说明                                                         |
| -------------------- | ------------------------------------------------------------ |
| is-primary           | 是否为主数据源（多个以第一个isPrimary为主，没有配置以第一个数据源为主） |
| enabled              | 是否启用该数据源                                             |
| driver-class-nameame | 数据库连接驱动                                               |
| url                  | 连接url                                                      |
| username             | 用户名                                                       |
| password             | 密码                                                         |
| pwdFile              | 密码文件(当设置了pwdFile优先从pwdFile中读取密码)             |
| decryption-name      | 密码解密算法名                                               |
| druid                | druid连接池相关配置                                          |
| hikari               | hikari连接池相关配置                                         |

### 代码

#### demo

```java
DynamicDataSourceSelector.use(DynamicDataSourceSelector.DEFAULT_DB);
Assertions.assertEquals("TEST0", testMapper.queryDbName());
DynamicDataSourceSelector.remove();
```

*注：`DynamicDataSourceSelector.use`使用时**在事务中切换是无效的**。只能用于非事务场景！！！

#### 扩展

##### 解码器

使用SPI在`META-INF.services`下增加`com.gill.datasources.decryption.DecryptionStrategy`文件，并写入对应实现类如`com.gill.datasources.decryption.DefaultDecryptionStrategy`

