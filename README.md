# 小咪楂 XIAOMIZHA 项目技术文档

## 项目概述

小咪楂 XIAOMIZHA 是一个基于 Spring Boot 的微服务架构项目，提供用户管理、权限控制、资源管理等功能。项目采用模块化设计，具有良好的可扩展性和维护性。

## 前端项目

本仓库为**纯后端**，不包含前端代码。前端为同层级独立项目 [**xiaomizha**](https://github.com/xuyouer/xiaomizha)（Vue 3 + Vite + Ant Design Vue），对接 `xuyou-user` 接口。

## 技术栈

### 核心技术

- **Spring Boot 3.x**：应用开发框架
- **Spring Cloud**：微服务架构支持
- **Eureka**：服务注册与发现
- **MyBatis Plus**：ORM框架，简化数据库操作
- **MySQL**：关系型数据库
- **Redis**：缓存服务
- **Druid**：数据库连接池，提供监控功能

### 工具与插件

- **Maven**：项目构建与依赖管理
- **Lombok**：简化Java代码
- **MyBatis X**：MyBatis代码生成工具

## 项目结构

```
XiaoMiZhaTest/
├── xuyou-common/                # 通用模块
│   ├── src/main/java/ltd/xiaomizha/xuyou/common/
│   │   ├── config/              # 配置类
│   │   ├── constant/            # 常量定义
│   │   ├── controller/          # 通用控制器
│   │   ├── enums/               # 枚举类
│   │   ├── exception/           # 异常处理
│   │   ├── handler/             # 处理器
│   │   ├── response/            # 响应封装
│   │   └── service/             # 通用服务
│   └── src/main/resources/      # 配置文件
├── xuyou-eureka/                # 服务注册中心
│   ├── src/main/java/ltd/xiaomizha/xuyou/eureka/
│   └── src/main/resources/      # 配置文件
├── xuyou-user/                  # 用户管理模块
│   ├── src/main/java/ltd/xiaomizha/xuyou/user/
│   │   ├── controller/          # 控制器
│   │   ├── dto/                 # 数据传输对象
│   │   ├── entity/              # 实体类
│   │   ├── mapper/              # Mapper接口
│   │   ├── service/             # 服务接口
│   │   └── XuyouUserApplication.java # 应用启动类
│   ├── src/main/resources/      # 配置文件
│   │   └── mapper/              # MyBatis XML映射文件
│   └── src/test/                # 测试代码
├── xiaomizha.sql                # 数据库初始化脚本
├── pom.xml                      # 父项目构建文件
└── README.md                    # 项目文档
```

## 模块说明

### 1. xuyou-common 模块

通用模块，提供项目中共享的配置、工具类和基础组件：

- **config/**：包含数据库配置、MyBatis Plus配置、Web配置等
- **constant/**：定义常量类，如分页常量、Servlet常量等
- **controller/**：通用控制器，如数据库监控控制器
- **enums/**：枚举类定义，如结果状态枚举、逻辑字段枚举等
- **exception/**：自定义异常类
- **handler/**：全局异常处理器、MyBatis元对象处理器
- **response/**：统一响应封装类
- **service/**：通用服务，如Druid监控服务

### 2. xuyou-eureka 模块

服务注册中心，基于Eureka实现：

- 提供服务注册与发现功能
- 支持服务健康检查
- 实现服务负载均衡

### 3. xuyou-user 模块

用户管理模块，核心业务功能：

- **用户管理**：用户信息CRUD操作
- **角色管理**：角色定义与权限分配
- **资源管理**：菜单、按钮、API等资源管理
- **权限控制**：基于角色的权限控制(RBAC)
- **用户反馈**：用户意见反馈管理
- **积分管理**：用户积分记录与管理
- **VIP管理**：用户VIP等级与特权管理

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+

### 数据库初始化

1. 创建数据库 `xiaomizha`
2. 执行 `xiaomizha.sql` 脚本初始化表结构

### 项目构建

```bash
# 克隆项目
git clone https://github.com/xuyouer/XiaoMiZhaTest.git

# 进入项目目录
cd XiaoMiZhaTest

# 构建项目
mvn clean install
```

### 服务启动顺序

1. **启动 Eureka 服务**
   ```bash
   cd xuyou-eureka
   mvn spring-boot:run
   ```

2. **启动 User 服务**
   ```bash
   cd xuyou-user
   mvn spring-boot:run
   ```

### 访问地址

- Eureka 控制台：http://localhost:8090
- User 服务 API：http://localhost:8092/api
- Druid 监控：http://localhost:8092/api/druid

## 核心功能

### 1. 用户管理

- 用户注册、登录、注销
- 用户信息查询与修改
- 用户状态管理
- 用户登录记录

### 2. 权限管理

- 角色创建与管理
- 资源权限分配
- 基于角色的权限控制
- 权限验证

### 3. 资源管理

- 菜单资源管理
- 按钮资源管理
- API资源管理
- 页面资源管理

### 4. 系统配置

- 系统参数配置
- 配置项管理

### 5. 监控功能

- Druid 数据库监控
- 系统运行状态监控

## API 文档

> 完整 API 自行查看 http://localhost:8092/api/doc.html

### 用户管理 API

| API路径                    | 方法   | 功能描述   |
|--------------------------|------|--------|
| `/api/users/list`        | GET  | 获取用户列表 |
| `/api/users/{id}`        | GET  | 获取用户详情 |
| `/api/users/register`    | POST | 创建用户   |
| `/api/users/update/{id}` | PUT  | 更新用户信息 |
| `/api/users/login`       | POST | 用户登录   |
| `/api/users/logout`      | POST | 用户注销   |

## 配置说明

> 完整配置文件查看 `xuyou-common/src/main/resources/**.yml`

### 主要配置文件

- **xuyou-common/src/main/resources/application-common.yml**：通用配置
- **xuyou-common/src/main/resources/application-mybatis-plus.yml**：MyBatis Plus配置
- **xuyou-common/src/main/resources/application-redis.yml**：Redis配置
- **xuyou-common/src/main/resources/application-druid.yml**：Druid配置
- **xuyou-eureka/src/main/resources/application.yml**：Eureka服务配置
- **xuyou-user/src/main/resources/application.yml**：用户服务配置

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiaomizha?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
```

## 开发指南

### 代码规范

1. **包命名**：使用小写字母，多层包之间用点分隔
2. **类命名**：使用驼峰命名法，首字母大写
3. **方法命名**：使用驼峰命名法，首字母小写
4. **变量命名**：使用驼峰命名法，首字母小写
5. **常量命名**：使用大写字母，单词之间用下划线分隔

### 常用命令

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn clean package

# 安装到本地仓库
mvn clean install

# 运行应用
mvn spring-boot:run
```

## 故障排除

### 常见问题

1. **服务启动失败**
    - 检查端口是否被占用
    - 检查数据库连接是否正常
    - 检查Redis连接是否正常

2. **API调用失败**
    - 检查服务是否正常运行
    - 检查请求参数是否正确
    - 检查权限是否足够

3. **数据库操作失败**
    - 检查SQL语句是否正确
    - 检查数据库连接是否正常
    - 检查事务是否正确处理

### 日志查看

- 服务日志：`logs/xuyou.log`
- Druid监控日志：通过Druid控制台查看
- Eureka日志：通过Eureka控制台查看

## 部署方案

### 本地开发环境

1. 安装JDK、Maven、MySQL、Redis
2. 克隆项目代码
3. 初始化数据库
4. 启动Eureka服务
5. 启动各个业务服务

### 生产环境

1. **容器化部署**：使用Docker容器化应用
2. **集群部署**：多实例部署提高可用性
3. **负载均衡**：使用Nginx或Spring Cloud LoadBalancer
4. **配置中心**：使用Spring Cloud Config管理配置
5. ~~**服务网关**：使用Spring Cloud Gateway统一入口~~

## 版本历史

| 版本    | 日期      | 描述           |
|-------|---------|--------------|
| 0.0.1 | 2026-01 | 项目初始化，完成核心功能 |

## 贡献指南

1. **Fork 项目**
2. **创建分支**：`git checkout -b feature/xxx`
3. **提交修改**：`git commit -m "添加xxx功能"`
4. **推送分支**：`git push origin feature/xxx`
5. **创建 Pull Request**

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目维护者：[xuyouer](https://github.com/xuyouer) & [zzx-super](https://github.com/zzx-super)
- 问题反馈：[GitHub Issues](https://github.com/xuyouer/XiaoMiZhaTest/issues)

---

**注意**：本文档仅供开发参考，具体实现以代码为准。