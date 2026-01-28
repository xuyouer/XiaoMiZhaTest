#!/bin/bash
# User 服务管理脚本
# 使用方法: ./user.sh {start|stop|restart|status}
# 部署目录: /opt/xuyou
APP_NAME="xuyou-user"
APP_JAR="xuyou-user-0.0.1.jar"
APP_PORT=8092
# 部署目录配置
APP_HOME="${APP_HOME:-/opt/xuyou}"
JAR_DIR="${JAR_DIR:-$APP_HOME/jar}"
LOG_DIR="${LOG_DIR:-$APP_HOME/log/user}"
PID_FILE="$APP_HOME/deploy/user.pid"
JAR_PATH="$JAR_DIR/$APP_JAR"
# 创建必要的目录
mkdir -p "$LOG_DIR"
mkdir -p "$JAR_DIR"
mkdir -p "$APP_HOME/deploy"
# 检查 Java 环境
check_java() {
    if ! command -v java &> /dev/null; then
        echo "错误: 未找到 Java，请先安装 Java 17+"
        exit 1
    fi
}
# 启动服务
start() {
    check_java

    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            echo "$APP_NAME 已经在运行中 (PID: $PID)"
            return 1
        else
            rm -f "$PID_FILE"
        fi
    fi

    if [ ! -f "$JAR_PATH" ]; then
        echo "错误: 找不到 JAR 文件: $JAR_PATH"
        echo "请将 $APP_JAR 文件放置到 $JAR_DIR 目录"
        exit 1
    fi

    echo "正在启动 $APP_NAME..."
    echo "JAR文件: $JAR_PATH"
    echo "日志目录: $LOG_DIR"

    # 设置日志文件路径
    LOG_FILE="$LOG_DIR/xuyou-user-all.log"

    # JVM参数配置（2核2G服务器优化）
    # User服务需要更多内存，分配512-768MB内存
    JAVA_OPTS="-Xms512m -Xmx768m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
    JAVA_OPTS="$JAVA_OPTS -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
    JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
    JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=$LOG_DIR/heapdump-user.hprof"
    JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"
    JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
    JAVA_OPTS="$JAVA_OPTS -DLOG_HOME=$LOG_DIR"

    # 启动服务（Spring Boot会自动读取application.yml中的配置）
    echo "JVM参数: $JAVA_OPTS"
    nohup java $JAVA_OPTS -jar "$JAR_PATH" > "$LOG_FILE" 2>&1 &
    PID=$!
    echo $PID > "$PID_FILE"

    # 等待进程启动，最多等待10秒
    echo "等待服务启动..."
    for i in {1..10}; do
        sleep 1
        if ! ps -p $PID > /dev/null 2>&1; then
            echo "警告: 进程 $PID 已退出，检查日志..."
            echo "=== 最近50行日志 ==="
            tail -50 "$LOG_FILE"
            echo "==================="
            rm -f "$PID_FILE"
            exit 1
        fi
        # 检查端口是否开始监听（表示服务已启动）
        if netstat -tuln 2>/dev/null | grep -q ":$APP_PORT " || ss -tuln 2>/dev/null | grep -q ":$APP_PORT "; then
            echo "$APP_NAME 启动成功 (PID: $PID)"
            echo "日志文件: $LOG_FILE"
            echo "访问地址: http://localhost:$APP_PORT/api"
            return 0
        fi
    done

    # 10秒后检查进程是否还在运行
    if ps -p $PID > /dev/null 2>&1; then
        echo "$APP_NAME 进程运行中 (PID: $PID)，但端口可能尚未就绪"
        echo "日志文件: $LOG_FILE"
        echo "请稍后检查服务状态: $0 status"
    else
        echo "$APP_NAME 启动失败，进程已退出"
        echo "=== 最近50行日志 ==="
        tail -50 "$LOG_FILE"
        echo "==================="
        rm -f "$PID_FILE"
        exit 1
    fi
}
# 停止服务
stop() {
    if [ ! -f "$PID_FILE" ]; then
        echo "$APP_NAME 未运行"
        return 1
    fi

    PID=$(cat "$PID_FILE")
    if ! ps -p $PID > /dev/null 2>&1; then
        echo "$APP_NAME 未运行"
        rm -f "$PID_FILE"
        return 1
    fi

    echo "正在停止 $APP_NAME (PID: $PID)..."
    kill $PID

    # 等待进程结束
    for i in {1..30}; do
        if ! ps -p $PID > /dev/null 2>&1; then
            break
        fi
        sleep 1
    done

    # 如果还在运行，强制杀死
    if ps -p $PID > /dev/null 2>&1; then
        echo "强制停止 $APP_NAME..."
        kill -9 $PID
    fi

    rm -f "$PID_FILE"
    echo "$APP_NAME 已停止"
}
# 重启服务
restart() {
    stop
    sleep 2
    start
}
# 查看状态
status() {
    if [ ! -f "$PID_FILE" ]; then
        echo "$APP_NAME 未运行"
        return 1
    fi

    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "$APP_NAME 正在运行 (PID: $PID)"
        echo "端口: $APP_PORT"

        # 检查端口是否监听
        if netstat -tuln 2>/dev/null | grep -q ":$APP_PORT " || ss -tuln 2>/dev/null | grep -q ":$APP_PORT "; then
            echo "端口 $APP_PORT 正在监听"
        else
            echo "警告: 端口 $APP_PORT 未监听"
        fi
    else
        echo "$APP_NAME 未运行"
        rm -f "$PID_FILE"
        return 1
    fi
}
# 主逻辑
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    *)
        echo "使用方法: $0 {start|stop|restart|status}"
        exit 1
        ;;
esac
exit 0