# 黑马商城 · hmall

Java 电商后端学习项目，同时保留 `hm-service` 单体实现与拆分后的微服务模块，覆盖商品、用户、购物车、交易、支付和搜索等业务。

仓库没有附带商城前端工程，也没有完整数据库初始化脚本。微服务运行需要自行准备配置中心和基础设施。

## 功能与模块

| 模块 | 作用 |
| --- | --- |
| `hm-common` | 公共工具、异常、用户上下文与公共配置 |
| `hm-api` | 服务接口、DTO、Feign 与消息相关定义 |
| `hm-service` | 保留的单体业务实现 |
| `hm-gateway` | 网关与请求转发 |
| `item-service` | 商品增删改查、上下架、库存扣减/恢复，保留部分搜索接口 |
| `user-service` | 登录、用户余额及地址查询 |
| `cart-service` | 购物车添加、修改、查询和删除 |
| `trade-service` | 创建订单、订单详情、支付状态及明细查询 |
| `pay-service` | 支付申请、余额支付与支付单查询 |
| `search-service` | Elasticsearch 商品搜索 |

## 技术栈

按当前父 POM：

- Java 11、Spring Boot 2.7.12。
- Spring Cloud 2021.0.3、Spring Cloud Alibaba 2021.0.4.0。
- MyBatis-Plus、MySQL、Hutool、Lombok。
- Nacos、OpenFeign、Gateway。
- RabbitMQ、Sentinel、Seata 相关依赖。
- Elasticsearch Java 客户端版本为 7.12.1；部署时应核对服务端兼容性。
- 单体模块还包含 Redis 依赖。

## 两种运行组织

### 单体模块

`hm-service` 保留商品、用户、地址、购物车、订单、支付和搜索控制器，默认端口 **8080**。配置位于：

- [application.yaml](hm-service/src/main/resources/application.yaml)
- [application-dev.yaml](hm-service/src/main/resources/application-dev.yaml)
- [application-local.yaml](hm-service/src/main/resources/application-local.yaml)

当前数据库名称为 `hmall`。需要先准备匹配的数据库表和业务数据，以及配置要求的外部服务。

### 微服务模块

| 服务 | 配置中的默认端口 |
| --- | --- |
| `hm-gateway` | 8080 |
| `item-service` | 8081 |
| `cart-service` | 8082 |
| `user-service` | 8084 |
| `trade-service` | 8085 |
| `pay-service` | 8086 |
| `search-service` | 8087 |

单体 `hm-service` 和网关默认都占用 8080，不要不加调整地同时启动两套入口。Nacos 下发配置也可能覆盖上述端口。

## 配置中心与基础设施

多数微服务使用模块内的 `bootstrap.yaml` 连接 Nacos，并引用以下共享配置：

```text
shared-jdbc.yaml
shared-log.yaml
shared-swagger.yaml
shared-seata.yaml
shared-mq.yaml
```

这些共享配置的完整内容没有随仓库提供，需要按各模块引用关系在 Nacos 中准备，核对命名空间、分组和实际服务地址。网关路由也需与当前配置中心中的规则匹配。

按所运行的模块准备 MySQL、Nacos，以及 RabbitMQ、Elasticsearch、Seata、Sentinel、Redis 等依赖。服务和组件是否必需，以对应 POM、配置及代码路径为准；不是所有模块都能只依靠一个数据库独立运行。

真实数据库密码、消息队列凭据、证书及令牌签名配置应由部署环境提供，不能直接复用仓库中的开发参数。

## 构建与启动

```shell
git clone https://github.com/Trappedqueens/hmall.git
cd hmall
mvn clean install -DskipTests
```

从根目录构建，确保 `hm-common`、`hm-api` 等本地模块依赖已安装。

单体示例：

```shell
java -jar hm-service/target/hm-service.jar
```

微服务示例（基础设施与配置准备完成后）：

```shell
java -jar item-service/target/item-service.jar
java -jar user-service/target/user-service.jar
java -jar cart-service/target/cart-service.jar
java -jar trade-service/target/trade-service.jar
java -jar pay-service/target/pay-service.jar
java -jar hm-gateway/target/hm-gateway.jar
```

也可在 IDE 中按对应模块启动类运行。当前 `search-service/pom.xml` 未单独声明 Spring Boot 打包插件，不能默认将其普通 JAR 当成可执行 Boot JAR；可先通过 IDE 启动 `com.hmall.search.SearchApplication`，或自行完善该模块打包配置。

## 接口分组

- `/users`：登录和余额操作。
- `/addresses`：地址查询。
- `/items`：商品和库存。
- `/carts`：购物车。
- `/orders`：订单。
- `/pay-orders`：支付单。
- `/search`：商品搜索。

登录、用户上下文与网关转发的认证方式应按所选运行模式检查，单体的 `authorization` 令牌与微服务的用户信息传递不能混为一谈。

## 当前边界

- 单体和微服务代码同时存在，不代表两套模式的配置可以直接通用。
- 没有配套前端和完整 SQL，启动前需要自行补齐。
- 仓库包含开发用证书文件，正式部署需使用自己管理的密钥与证书。
- 部分测试需要外部服务和索引数据，运行前应使用独立环境。
- 本次只新增 README，未修改源代码、Nacos 配置或数据库，也没有执行构建和端到端测试。
