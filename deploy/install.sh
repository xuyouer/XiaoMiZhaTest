#!/bin/bash
# 一键安装部署脚本
# 使用方法: ./install.sh
set -e
APP_HOME="/opt/xuyou"
CURRENT_USER=$(whoami)
echo "=========================================="
echo "Xuyou 服务部署安装脚本"
echo "=========================================="
# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo "警告: 建议使用root用户运行此脚本"
    read -p "是否继续? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi
# 1. 创建目录结构
echo "1. 创建目录结构..."
mkdir -p "$APP_HOME"/{jar,log/{eureka,user},config,deploy}
echo "✓ 目录创建完成"
# 2. 设置目录权限
echo "2. 设置目录权限..."
chown -R $CURRENT_USER:$CURRENT_USER "$APP_HOME"
chmod -R 755 "$APP_HOME"
chmod -R 755 "$APP_HOME/deploy"
echo "✓ 权限设置完成"
# 3. 检查Java环境
echo "3. 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "✗ 未找到Java，请先安装Java 17+"
    echo "安装命令:"
    echo "  CentOS/RHEL: yum install -y java-17-openjdk java-17-openjdk-devel"
    echo "  Ubuntu/Debian: apt-get install -y openjdk-17-jdk"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "✓ Java环境: $JAVA_VERSION"
# 4. 创建环境变量配置文件模板
echo "4. 创建环境变量配置文件..."
if [ ! -f "$APP_HOME/config/env.sh" ]; then
    cat > "$APP_HOME/config/env.sh" << 'EOF'
#!/bin/bash
# Java配置
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk}
export PATH=$JAVA_HOME/bin:$PATH
# 应用配置
export APP_HOME=/opt/xuyou
export JAR_DIR=$APP_HOME/jar
export LOG_DIR=$APP_HOME/log
# 数据库配置（请修改为实际值）
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
# JVM参数
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
EOF
    chmod +x "$APP_HOME/config/env.sh"
    echo "✓ 环境变量配置文件已创建: $APP_HOME/config/env.sh"
    echo "  请编辑此文件，配置数据库密码等敏感信息"
else
    echo "✓ 环境变量配置文件已存在"
fi
# 5. 复制部署脚本
echo "5. 复制部署脚本..."
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -f "$SCRIPT_DIR/eureka.sh" ]; then
    cp "$SCRIPT_DIR/eureka.sh" "$APP_HOME/deploy/"
    chmod +x "$APP_HOME/deploy/eureka.sh"
    echo "✓ Eureka部署脚本已复制"
fi
if [ -f "$SCRIPT_DIR/user.sh" ]; then
    cp "$SCRIPT_DIR/user.sh" "$APP_HOME/deploy/"
    chmod +x "$APP_HOME/deploy/user.sh"
    echo "✓ User部署脚本已复制"
fi
# 6. 创建systemd服务文件（可选）
echo "6. 创建systemd服务文件..."
read -p "是否创建systemd服务文件? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    # Eureka服务
    cat > /tmp/xuyou-eureka.service << EOF
[Unit]
Description=Xuyou Eureka Service
After=network.target
[Service]
Type=forking
User=$CURRENT_USER
Group=$CURRENT_USER
WorkingDirectory=$APP_HOME
EnvironmentFile=$APP_HOME/config/env.sh
ExecStart=$APP_HOME/deploy/eureka.sh start
ExecStop=$APP_HOME/deploy/eureka.sh stop
ExecReload=$APP_HOME/deploy/eureka.sh restart
PIDFile=$APP_HOME/deploy/eureka.pid
Restart=on-failure
RestartSec=10
[Install]
WantedBy=multi-user.target
EOF
    # User服务
    cat > /tmp/xuyou-user.service << EOF
[Unit]
Description=Xuyou User Service
After=network.target xuyou-eureka.service
Requires=xuyou-eureka.service
[Service]
Type=forking
User=$CURRENT_USER
Group=$CURRENT_USER
WorkingDirectory=$APP_HOME
EnvironmentFile=$APP_HOME/config/env.sh
ExecStart=$APP_HOME/deploy/user.sh start
ExecStop=$APP_HOME/deploy/user.sh stop
ExecReload=$APP_HOME/deploy/user.sh restart
PIDFile=$APP_HOME/deploy/user.pid
Restart=on-failure
RestartSec=10
[Install]
WantedBy=multi-user.target
EOF
    sudo cp /tmp/xuyou-eureka.service /etc/systemd/system/
    sudo cp /tmp/xuyou-user.service /etc/systemd/system/
    sudo systemctl daemon-reload
    echo "✓ systemd服务文件已创建"
    echo "  使用以下命令管理服务:"
    echo "    sudo systemctl start xuyou-eureka"
    echo "    sudo systemctl start xuyou-user"
fi
echo ""
echo "=========================================="
echo "安装完成！"
echo "=========================================="
echo ""
echo "下一步操作:"
echo "1. 编辑环境变量配置文件:"
echo "   vi $APP_HOME/config/env.sh"
echo ""
echo "2. 上传JAR包到以下目录:"
echo "   $APP_HOME/jar/xuyou-eureka-0.0.1.jar"
echo "   $APP_HOME/jar/xuyou-user-0.0.1.jar"
echo ""
echo "3. 启动服务:"
echo "   cd $APP_HOME/deploy"
echo "   source ../config/env.sh"
echo "   ./eureka.sh start"
echo "   ./user.sh start"
echo ""
echo "4. 查看日志:"
echo "   tail -f $APP_HOME/log/eureka/xuyou-eureka-all.log"
echo "   tail -f $APP_HOME/log/user/xuyou-user-all.log"
echo ""