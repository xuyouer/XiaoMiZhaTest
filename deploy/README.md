# 快速部署指南

## 目录结构

```
/opt/xuyou/
├── jar/                    # JAR包目录
│   ├── xuyou-eureka-0.0.1.jar
│   └── xuyou-user-0.0.1.jar
├── log/                    # 日志文件目录
│   ├── eureka/            # Eureka服务日志
│   └── user/              # User服务日志
├── config/                 # 配置文件目录
│   └── env.sh             # 环境变量配置
└── deploy/                 # 部署脚本目录
    ├── eureka.sh          # Eureka服务管理脚本
    ├── user.sh            # User服务管理脚本
    ├── install.sh         # 一键安装脚本
    ├── eureka.pid         # Eureka进程ID文件
    └── user.pid           # User进程ID文件
```

## 快速开始

### 1. 一键安装（推荐）

```bash
# 上传install.sh到服务器
scp deploy/install.sh user@server:/tmp/
# SSH登录服务器执行
ssh user@server
cd /tmp
chmod +x install.sh
sudo ./install.sh
```

### 2. 手动安装

```bash
# 创建目录
sudo mkdir -p /opt/xuyou/{jar,log/{eureka,user},config,deploy}
sudo chown -R $USER:$USER /opt/xuyou
# 上传文件
scp xuyou-eureka/target/xuyou-eureka-0.0.1.jar user@server:/opt/xuyou/jar/
scp xuyou-user/target/xuyou-user-0.0.1.jar user@server:/opt/xuyou/jar/
scp deploy/*.sh user@server:/opt/xuyou/deploy/
# 设置权限
ssh user@server "chmod +x /opt/xuyou/deploy/*.sh"
```

### 3. 配置环境变量

```bash
# 编辑环境变量文件
vi /opt/xuyou/config/env.sh
# 必须修改以下配置：
# - DB_PASSWORD: 数据库密码
# - DRUID_PASSWORD: Druid监控密码
# - RABBITMQ_PASSWORD: RabbitMQ密码（如果使用）
```

### 4. 启动服务

```bash
cd /opt/xuyou/deploy
source ../config/env.sh
# 先启动Eureka
./eureka.sh start
# 等待10-15秒后启动User服务
sleep 15
./user.sh start
```

## 服务管理

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

## 查看日志

```bash
# 实时查看日志
tail -f /opt/xuyou/log/eureka/xuyou-eureka-all.log
tail -f /opt/xuyou/log/user/xuyou-user-all.log
# 查看错误日志
tail -f /opt/xuyou/log/eureka/xuyou-eureka-error.log
tail -f /opt/xuyou/log/user/xuyou-user-error.log
```

## 健康检查

```bash
# 检查Eureka服务
curl http://localhost:8090/actuator/health
# 检查User服务
curl http://localhost:8092/api/actuator/health
# 访问Eureka控制台
curl http://localhost:8090/
# 访问API文档
curl http://localhost:8092/api/doc.html
```

## 常见问题

### 服务启动失败

1. 检查Java版本: `java -version`（需要Java 17+）
2. 检查JAR文件是否存在: `ls -lh /opt/xuyou/jar/*.jar`
3. 查看启动日志: `tail -100 /opt/xuyou/log/*/xuyou-*-all.log`

### 端口被占用

```bash
# 检查端口占用
netstat -tuln | grep -E '8090|8092'
# 或
ss -tuln | grep -E '8090|8092'
# 杀死占用进程
kill -9 <PID>
```

### 数据库连接失败

1. 检查数据库服务是否运行
2. 验证环境变量配置: `cat /opt/xuyou/config/env.sh | grep DB_`
3. 测试网络连接: `telnet 106.15.72.90 3306`

## 详细文档

更多详细信息请参考: `docs/DEPLOYMENT.md`