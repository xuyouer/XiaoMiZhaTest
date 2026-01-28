# 服务器部署文档

## 目录结构

```
/opt/xuyou/
├── jar/                    # JAR包目录
│   ├── xuyou-eureka-0.0.1.jar
│   └── xuyou-user-0.0.1.jar
├── log/                    # 日志文件目录
│   ├── eureka/
│   │   ├── xuyou-eureka-all.log
│   │   ├── xuyou-eureka-error.log
│   │   └── xuyou-eureka-warn.log
│   └── user/
│       ├── xuyou-user-all.log
│       ├── xuyou-user-error.log
│       └── xuyou-user-warn.log
├── config/                 # 配置文件目录（可选）
│   └── application-prod.yml
└── deploy/                 # 部署脚本目录
    ├── eureka.sh
    ├── user.sh
    ├── eureka.pid
    └── user.pid
```

## 环境要求

### 系统要求

- **操作系统**: Linux (CentOS 7+, Ubuntu 18.04+)
- **Java版本**: JDK 17 或更高版本
- **内存**: 建议至少 4GB 可用内存
- **磁盘空间**: 建议至少 10GB 可用空间

### 依赖服务

- **MySQL**: 5.7+ 或 8.0+
- **Redis**: 6.0+（可选）
- **RabbitMQ**: 3.8+（可选）
- **Eureka**: 项目内置

## 部署步骤

### 1. 创建目录结构

```bash
# 创建主目录
sudo mkdir -p /opt/xuyou/{jar,log/{eureka,user},config,deploy}
# 设置目录权限
sudo chown -R $USER:$USER /opt/xuyou
chmod -R 755 /opt/xuyou
```

### 2. 安装 Java 17

```bash
# CentOS/RHEL
sudo yum install -y java-17-openjdk java-17-openjdk-devel
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk
# 验证安装
java -version
```

### 3. 配置环境变量

创建环境变量配置文件 `/opt/xuyou/config/env.sh`:

```bash
#!/bin/bash
# Java配置
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
# 应用配置
export APP_HOME=/opt/xuyou
export JAR_DIR=$APP_HOME/jar
export LOG_DIR=$APP_HOME/log
# 数据库配置（生产环境必须配置）
export DB_URL=jdbc:mysql://106.15.72.90:3306/xiaomizha?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
export DB_USERNAME=xiaomizha
export DB_PASSWORD=your_password_here
# Druid监控配置
export DRUID_USERNAME=admin
export DRUID_PASSWORD=your_druid_password_here
# RabbitMQ配置
export RABBITMQ_HOST=106.15.72.90
export RABBITMQ_PORT=5672
export RABBITMQ_USERNAME=admin
export RABBITMQ_PASSWORD=admin
# Redis配置（如果使用）
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
# 日志配置
export LOG_HOME=/opt/xuyou/log
export LOG_LEVEL_APP=INFO
export LOG_LEVEL_ROOT=INFO
# 雪花算法配置
export SNOWFLAKE_WORKER_ID=1
export SNOWFLAKE_DATACENTER_ID=1
```

设置执行权限：

```bash
chmod +x /opt/xuyou/config/env.sh
```

### 4. 编译打包项目

在开发机器上执行：

```bash
# 进入项目目录
cd /path/to/xuyou
# 清理并打包（跳过测试）
mvn clean package -DskipTests
# 打包完成后，JAR文件位于：
# - xuyou-eureka/target/xuyou-eureka-0.0.1.jar
# - xuyou-user/target/xuyou-user-0.0.1.jar
```

### 5. 上传文件到服务器

```bash
# 上传JAR包
scp xuyou-eureka/target/xuyou-eureka-0.0.1.jar user@server:/opt/xuyou/jar/
scp xuyou-user/target/xuyou-user-0.0.1.jar user@server:/opt/xuyou/jar/
# 上传部署脚本
scp deploy/eureka.sh user@server:/opt/xuyou/deploy/
scp deploy/user.sh user@server:/opt/xuyou/deploy/
# 设置脚本执行权限
ssh user@server "chmod +x /opt/xuyou/deploy/*.sh"
```

### 6. 配置服务

#### 6.1 配置数据库连接

编辑环境变量文件，设置正确的数据库连接信息：

```bash
vi /opt/xuyou/config/env.sh
# 修改 DB_URL, DB_USERNAME, DB_PASSWORD
```

#### 6.2 配置Eureka服务地址

如果Eureka和User服务不在同一台服务器，需要修改User服务的Eureka地址：

```bash
# 编辑User服务配置
vi /opt/xuyou/config/application-prod.yml
```

添加内容：

```yaml
eureka:
  instance:
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://eureka-server-ip:8090/eureka/
```

### 7. 启动服务

#### 7.1 启动顺序

**重要**: 必须先启动Eureka服务，再启动User服务。

```bash
# 1. 启动Eureka服务
cd /opt/xuyou/deploy
source ../config/env.sh
./eureka.sh start
# 等待Eureka启动完成（约10-30秒）
sleep 15
# 2. 启动User服务
./user.sh start
```

#### 7.2 验证服务状态

```bash
# 检查Eureka服务
./eureka.sh status
# 检查User服务
./user.sh status
# 查看日志
tail -f /opt/xuyou/log/eureka/xuyou-eureka-all.log
tail -f /opt/xuyou/log/user/xuyou-user-all.log
```

### 8. 配置防火墙

```bash
# 开放Eureka端口
sudo firewall-cmd --permanent --add-port=8090/tcp
# 开放User服务端口
sudo firewall-cmd --permanent --add-port=8092/tcp
# 重新加载防火墙
sudo firewall-cmd --reload
# 或者使用iptables
sudo iptables -A INPUT -p tcp --dport 8090 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 8092 -j ACCEPT
```

### 9. 配置系统服务（可选）

#### 9.1 创建systemd服务文件

创建 `/etc/systemd/system/xuyou-eureka.service`:

```ini
[Unit]
Description=Xuyou Eureka Service
After=network.target
[Service]
Type=forking
User=root
Group=root
WorkingDirectory=/opt/xuyou
EnvironmentFile=/opt/xuyou/config/env.sh
ExecStart=/opt/xuyou/deploy/eureka.sh start
ExecStop=/opt/xuyou/deploy/eureka.sh stop
ExecReload=/opt/xuyou/deploy/eureka.sh restart
PIDFile=/opt/xuyou/deploy/eureka.pid
Restart=on-failure
RestartSec=10
[Install]
WantedBy=multi-user.target
```

创建 `/etc/systemd/system/xuyou-user.service`:

```ini
[Unit]
Description=Xuyou User Service
After=network.target xuyou-eureka.service
Requires=xuyou-eureka.service
[Service]
Type=forking
User=root
Group=root
WorkingDirectory=/opt/xuyou
EnvironmentFile=/opt/xuyou/config/env.sh
ExecStart=/opt/xuyou/deploy/user.sh start
ExecStop=/opt/xuyou/deploy/user.sh stop
ExecReload=/opt/xuyou/deploy/user.sh restart
PIDFile=/opt/xuyou/deploy/user.pid
Restart=on-failure
RestartSec=10
[Install]
WantedBy=multi-user.target
```

#### 9.2 启用并启动服务

```bash
# 重新加载systemd配置
sudo systemctl daemon-reload
# 启用服务（开机自启）
sudo systemctl enable xuyou-eureka
sudo systemctl enable xuyou-user
# 启动服务
sudo systemctl start xuyou-eureka
sudo systemctl start xuyou-user
# 查看状态
sudo systemctl status xuyou-eureka
sudo systemctl status xuyou-user
```

## 服务管理

### 使用部署脚本

```bash
# 启动服务
./eureka.sh start
./user.sh start
# 停止服务
./eureka.sh stop
./user.sh stop
# 重启服务
./eureka.sh restart
./user.sh restart
# 查看状态
./eureka.sh status
./user.sh status
```

### 使用systemd（如果配置了）

```bash
# 启动服务
sudo systemctl start xuyou-eureka
sudo systemctl start xuyou-user
# 停止服务
sudo systemctl stop xuyou-eureka
sudo systemctl stop xuyou-user
# 重启服务
sudo systemctl restart xuyou-eureka
sudo systemctl restart xuyou-user
# 查看状态
sudo systemctl status xuyou-eureka
sudo systemctl status xuyou-user
# 查看日志
sudo journalctl -u xuyou-eureka -f
sudo journalctl -u xuyou-user -f
```

## 健康检查

### 检查服务是否运行

```bash
# 检查进程
ps aux | grep xuyou
# 检查端口
netstat -tuln | grep -E '8090|8092'
# 或
ss -tuln | grep -E '8090|8092'
```

### 检查服务健康状态

```bash
# Eureka服务健康检查
curl http://localhost:8090/actuator/health
# User服务健康检查
curl http://localhost:8092/api/actuator/health
# Eureka控制台
curl http://localhost:8090/
# User服务API文档
curl http://localhost:8092/api/doc.html
```

## 日志管理

### 查看日志

```bash
# 实时查看Eureka日志
tail -f /opt/xuyou/log/eureka/xuyou-eureka-all.log
# 实时查看User日志
tail -f /opt/xuyou/log/user/xuyou-user-all.log
# 查看错误日志
tail -f /opt/xuyou/log/eureka/xuyou-eureka-error.log
tail -f /opt/xuyou/log/user/xuyou-user-error.log
# 搜索日志
grep "ERROR" /opt/xuyou/log/user/xuyou-user-all.log
```

### 日志清理

日志文件会自动滚动和清理，但也可以手动清理：

```bash
# 清理30天前的日志
find /opt/xuyou/log -name "*.log.*" -mtime +30 -delete
# 清理空日志文件
find /opt/xuyou/log -name "*.log" -size 0 -delete
```

## 监控和维护

### 监控指标

1. **服务状态**: 定期检查服务是否运行
2. **端口监听**: 检查服务端口是否正常监听
3. **日志文件**: 定期查看错误日志
4. **磁盘空间**: 监控日志目录磁盘使用情况
5. **内存使用**: 监控JVM内存使用情况

### 性能调优

#### JVM参数调优

编辑部署脚本，修改JVM参数：

```bash
# 修改启动命令中的JVM参数
java -Xms1g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/opt/xuyou/log/heapdump.hprof \
     -jar "$JAR_PATH"
```

#### 数据库连接池调优

编辑 `application-druid.yml`，调整连接池参数：

```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
```

## 常见问题

### 1. 服务启动失败

**问题**: 服务无法启动
**排查步骤**:

1. 检查Java版本: `java -version`
2. 检查JAR文件是否存在: `ls -lh /opt/xuyou/jar/*.jar`
3. 查看启动日志: `tail -100 /opt/xuyou/log/*/xuyou-*-all.log`
4. 检查端口是否被占用: `netstat -tuln | grep 8090`

### 2. 数据库连接失败

**问题**: 无法连接到数据库
**排查步骤**:

1. 检查数据库服务是否运行
2. 检查网络连接: `telnet 106.15.72.90 3306`
3. 验证数据库用户名和密码
4. 检查防火墙规则

### 3. Eureka注册失败

**问题**: User服务无法注册到Eureka
**排查步骤**:

1. 确认Eureka服务已启动
2. 检查Eureka地址配置是否正确
3. 检查网络连接
4. 查看Eureka控制台: `http://eureka-server:8090`

### 4. 内存不足

**问题**: OutOfMemoryError
**解决方案**:

1. 增加JVM堆内存: 修改启动脚本中的 `-Xmx` 参数
2. 检查是否有内存泄漏
3. 优化代码，减少内存使用

### 5. 日志文件过大

**问题**: 日志文件占用大量磁盘空间
**解决方案**:

1. 检查日志滚动配置
2. 手动清理旧日志文件
3. 调整日志级别，减少日志输出

## 备份和恢复

### 备份

```bash
# 备份配置文件
tar -czf xuyou-config-backup-$(date +%Y%m%d).tar.gz /opt/xuyou/config
# 备份日志（可选）
tar -czf xuyou-logs-backup-$(date +%Y%m%d).tar.gz /opt/xuyou/log
```

### 恢复

```bash
# 恢复配置文件
tar -xzf xuyou-config-backup-YYYYMMDD.tar.gz -C /
```

## 升级部署

### 升级步骤

1. **备份当前版本**
   ```bash
   cp /opt/xuyou/jar/xuyou-user-0.0.1.jar /opt/xuyou/jar/xuyou-user-0.0.1.jar.bak
   ```
2. **停止服务**
   ```bash
   ./user.sh stop
   ```
3. **上传新版本JAR包**
   ```bash
   scp xuyou-user/target/xuyou-user-0.0.1.jar user@server:/opt/xuyou/jar/
   ```
4. **启动服务**
   ```bash
   ./user.sh start
   ```
5. **验证服务**
   ```bash
   ./user.sh status
   curl http://localhost:8092/api/actuator/health
   ```

## 安全建议

1. **修改默认密码**: 修改Druid监控、数据库等默认密码
2. **使用环境变量**: 生产环境使用环境变量配置敏感信息
3. **限制访问**: 配置防火墙，只开放必要端口
4. **定期更新**: 定期更新依赖包，修复安全漏洞
5. **日志审计**: 定期审查日志，发现异常访问

## 联系支持

如遇到问题，请查看：

- 日志文件: `/opt/xuyou/log/`
- 项目文档: `docs/` 目录
- 重构说明: `REFACTORING.md`