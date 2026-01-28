#!/bin/bash
###############################################################################
# Xuyou 项目环境变量配置文件
# 位置: /opt/xuyou/config/env.sh
# 使用方法: source /opt/xuyou/config/env.sh
###############################################################################
# ============================================================================
# Java 环境配置
# ============================================================================
# Java安装路径（根据实际安装路径修改）
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk}
# 如果JAVA_HOME未设置，尝试自动检测
if [ ! -d "$JAVA_HOME" ]; then
    # 尝试常见的Java安装路径
    if [ -d "/usr/lib/jvm/java-17-openjdk" ]; then
        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
    elif [ -d "/usr/lib/jvm/java-17" ]; then
        export JAVA_HOME=/usr/lib/jvm/java-17
    elif [ -d "/opt/java" ]; then
        export JAVA_HOME=/opt/java
    fi
fi
export PATH=$JAVA_HOME/bin:$PATH
# ============================================================================
# 应用目录配置
# ============================================================================
export APP_HOME=/opt/xuyou
export JAR_DIR=$APP_HOME/jar
export LOG_DIR=$APP_HOME/log
export CONFIG_DIR=$APP_HOME/config
export DEPLOY_DIR=$APP_HOME/deploy
# ============================================================================
# 数据库配置（生产环境必须配置）
# ============================================================================
# 数据库连接URL
export DB_URL=${DB_URL:-jdbc:mysql://106.15.72.90:3306/xiaomizha?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true}
# 数据库用户名
export DB_USERNAME=${DB_USERNAME:-xiaomizha}
# 数据库密码（生产环境必须修改）
export DB_PASSWORD=${DB_PASSWORD:-your_password_here}
# ============================================================================
# Druid 监控配置
# ============================================================================
# Druid监控登录用户名
export DRUID_USERNAME=${DRUID_USERNAME:-admin}
# Druid监控登录密码（生产环境必须修改）
export DRUID_PASSWORD=${DRUID_PASSWORD:-your_druid_password_here}
# Druid监控允许访问的IP（多个IP用逗号分隔）
export DRUID_ALLOW=${DRUID_ALLOW:-127.0.0.1}
# Druid监控拒绝访问的IP（多个IP用逗号分隔）
export DRUID_DENY=${DRUID_DENY:-}
# ============================================================================
# RabbitMQ 配置
# ============================================================================
# RabbitMQ服务器地址
export RABBITMQ_HOST=${RABBITMQ_HOST:-106.15.72.90}
# RabbitMQ端口
export RABBITMQ_PORT=${RABBITMQ_PORT:-5672}
# RabbitMQ用户名
export RABBITMQ_USERNAME=${RABBITMQ_USERNAME:-admin}
# RabbitMQ密码
export RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD:-admin}
# RabbitMQ虚拟主机
export RABBITMQ_VIRTUAL_HOST=${RABBITMQ_VIRTUAL_HOST:-/}
# RabbitMQ连接超时时间（毫秒）
export RABBITMQ_CONNECTION_TIMEOUT=${RABBITMQ_CONNECTION_TIMEOUT:-15000}
# RabbitMQ发布确认类型
export RABBITMQ_PUBLISHER_CONFIRM_TYPE=${RABBITMQ_PUBLISHER_CONFIRM_TYPE:-correlated}
# RabbitMQ发布返回
export RABBITMQ_PUBLISHER_RETURNS=${RABBITMQ_PUBLISHER_RETURNS:-true}
# RabbitMQ模板强制消息持久化
export RABBITMQ_TEMPLATE_MANDATORY=${RABBITMQ_TEMPLATE_MANDATORY:-true}
# RabbitMQ模板接收超时时间（毫秒）
export RABBITMQ_TEMPLATE_RECEIVE_TIMEOUT=${RABBITMQ_TEMPLATE_RECEIVE_TIMEOUT:-3000}
# RabbitMQ模板回复超时时间（毫秒）
export RABBITMQ_TEMPLATE_REPLY_TIMEOUT=${RABBITMQ_TEMPLATE_REPLY_TIMEOUT:-5000}
# RabbitMQ监听器确认模式
export RABBITMQ_LISTENER_ACKNOWLEDGE_MODE=${RABBITMQ_LISTENER_ACKNOWLEDGE_MODE:-AUTO}
# RabbitMQ监听器并发数
export RABBITMQ_LISTENER_CONCURRENCY=${RABBITMQ_LISTENER_CONCURRENCY:-1}
# RabbitMQ监听器最大并发数
export RABBITMQ_LISTENER_MAX_CONCURRENCY=${RABBITMQ_LISTENER_MAX_CONCURRENCY:-10}
# RabbitMQ监听器预取数量
export RABBITMQ_LISTENER_PREFETCH=${RABBITMQ_LISTENER_PREFETCH:-1}
# RabbitMQ监听器自动启动
export RABBITMQ_LISTENER_AUTO_STARTUP=${RABBITMQ_LISTENER_AUTO_STARTUP:-true}
# RabbitMQ监听器默认重新入队
export RABBITMQ_LISTENER_DEFAULT_REQUEUE_REJECTED=${RABBITMQ_LISTENER_DEFAULT_REQUEUE_REJECTED:-false}
# RabbitMQ监听器重试启用
export RABBITMQ_LISTENER_RETRY_ENABLED=${RABBITMQ_LISTENER_RETRY_ENABLED:-false}
# RabbitMQ监听器重试初始间隔（毫秒）
export RABBITMQ_LISTENER_RETRY_INITIAL_INTERVAL=${RABBITMQ_LISTENER_RETRY_INITIAL_INTERVAL:-1000}
# RabbitMQ监听器重试最大间隔（毫秒）
export RABBITMQ_LISTENER_RETRY_MAX_INTERVAL=${RABBITMQ_LISTENER_RETRY_MAX_INTERVAL:-10000}
# RabbitMQ监听器重试倍数
export RABBITMQ_LISTENER_RETRY_MULTIPLIER=${RABBITMQ_LISTENER_RETRY_MULTIPLIER:-1.0}
# RabbitMQ监听器重试最大次数
export RABBITMQ_LISTENER_RETRY_MAX_ATTEMPTS=${RABBITMQ_LISTENER_RETRY_MAX_ATTEMPTS:-3}
# RabbitMQ连接池大小
export RABBITMQ_CACHE_CONNECTION_SIZE=${RABBITMQ_CACHE_CONNECTION_SIZE:-1}
# RabbitMQ连接池模式
export RABBITMQ_CACHE_CONNECTION_MODE=${RABBITMQ_CACHE_CONNECTION_MODE:-CHANNEL}
# RabbitMQ通道缓存大小
export RABBITMQ_CACHE_CHANNEL_SIZE=${RABBITMQ_CACHE_CHANNEL_SIZE:-25}
# RabbitMQ通道检查超时时间（毫秒）
export RABBITMQ_CACHE_CHANNEL_CHECKOUT_TIMEOUT=${RABBITMQ_CACHE_CHANNEL_CHECKOUT_TIMEOUT:-200}
# ============================================================================
# Redis 配置
# ============================================================================
# Redis服务器地址
export REDIS_HOST=${REDIS_HOST:-localhost}
# Redis端口
export REDIS_PORT=${REDIS_PORT:-6379}
# Redis密码（如果设置了密码）
export REDIS_PASSWORD=${REDIS_PASSWORD:-}
# Redis数据库索引（0-15）
export REDIS_DATABASE=${REDIS_DATABASE:-0}
# Redis连接超时时间（毫秒）
export REDIS_TIMEOUT=${REDIS_TIMEOUT:-3000}
# Redis连接池最大活跃连接数
export REDIS_POOL_MAX_ACTIVE=${REDIS_POOL_MAX_ACTIVE:-20}
# Redis连接池最大空闲连接数
export REDIS_POOL_MAX_IDLE=${REDIS_POOL_MAX_IDLE:-10}
# Redis连接池最小空闲连接数
export REDIS_POOL_MIN_IDLE=${REDIS_POOL_MIN_IDLE:-5}
# Redis连接池最大等待时间（毫秒）
export REDIS_POOL_MAX_WAIT=${REDIS_POOL_MAX_WAIT:-3000}
# Redis缓存过期时间（毫秒，默认1小时）
export REDIS_CACHE_TTL=${REDIS_CACHE_TTL:-3600000}
# ============================================================================
# 日志配置
# ============================================================================
# 日志文件存储路径
export LOG_HOME=${LOG_HOME:-/opt/xuyou/log}
# 应用日志级别（DEBUG, INFO, WARN, ERROR）
export LOG_LEVEL_APP=${LOG_LEVEL_APP:-INFO}
# Spring框架日志级别
export LOG_LEVEL_SPRING=${LOG_LEVEL_SPRING:-INFO}
# Web日志级别
export LOG_LEVEL_WEB=${LOG_LEVEL_WEB:-INFO}
# MyBatis日志级别
export LOG_LEVEL_MYBATIS=${LOG_LEVEL_MYBATIS:-INFO}
# 根日志级别
export LOG_LEVEL_ROOT=${LOG_LEVEL_ROOT:-INFO}
# 日志文件保留天数
export LOG_MAX_HISTORY=${LOG_MAX_HISTORY:-30}
# 单个日志文件最大大小
export LOG_MAX_FILE_SIZE=${LOG_MAX_FILE_SIZE:-100MB}
# 所有日志文件总大小限制
export LOG_TOTAL_SIZE_CAP=${LOG_TOTAL_SIZE_CAP:-10GB}
# ============================================================================
# 雪花算法配置
# ============================================================================
# 工作机器ID（0-31）
export SNOWFLAKE_WORKER_ID=${SNOWFLAKE_WORKER_ID:-1}
# 数据中心ID（0-31）
export SNOWFLAKE_DATACENTER_ID=${SNOWFLAKE_DATACENTER_ID:-1}
# ============================================================================
# JVM 参数配置
# ============================================================================
# 初始堆内存大小
export JVM_XMS=${JVM_XMS:-512m}
# 最大堆内存大小
export JVM_XMX=${JVM_XMX:-1024m}
# 元空间大小
export JVM_METASPACE=${JVM_METASPACE:-256m}
# 直接内存大小
export JVM_DIRECT_MEMORY=${JVM_DIRECT_MEMORY:-128m}
# GC类型（G1GC, ParallelGC, CMS等）
export JVM_GC_TYPE=${JVM_GC_TYPE:-G1GC}
# GC最大暂停时间（毫秒）
export JVM_GC_MAX_PAUSE=${JVM_GC_MAX_PAUSE:-200}
# 是否启用GC日志
export JVM_GC_LOG_ENABLED=${JVM_GC_LOG_ENABLED:-true}
# GC日志文件路径
export JVM_GC_LOG_FILE=${JVM_GC_LOG_FILE:-$LOG_DIR/gc.log}
# 是否在OOM时生成堆转储
export JVM_HEAP_DUMP_ON_OOM=${JVM_HEAP_DUMP_ON_OOM:-true}
# 堆转储文件路径
export JVM_HEAP_DUMP_PATH=${JVM_HEAP_DUMP_PATH:-$LOG_DIR/heapdump.hprof}
# 构建完整的JVM参数
export JAVA_OPTS="-Xms${JVM_XMS} -Xmx${JVM_XMX} \
    -XX:MetaspaceSize=${JVM_METASPACE} -XX:MaxMetaspaceSize=${JVM_METASPACE} \
    -XX:MaxDirectMemorySize=${JVM_DIRECT_MEMORY} \
    -XX:+Use${JVM_GC_TYPE} \
    -XX:MaxGCPauseMillis=${JVM_GC_MAX_PAUSE} \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=${JVM_HEAP_DUMP_PATH} \
    -XX:+PrintGCDetails \
    -XX:+PrintGCDateStamps \
    -Xloggc:${JVM_GC_LOG_FILE} \
    -XX:+UseGCLogFileRotation \
    -XX:NumberOfGCLogFiles=10 \
    -XX:GCLogFileSize=10M \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Shanghai"
# ============================================================================
# Spring Boot 配置
# ============================================================================
# Spring Profile（dev, test, prod）
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
# Spring应用名称
export SPRING_APPLICATION_NAME=${SPRING_APPLICATION_NAME:-xuyou}
# ============================================================================
# Eureka 配置
# ============================================================================
# Eureka服务器地址（如果User服务和Eureka不在同一台服务器，需要修改）
export EUREKA_SERVER_URL=${EUREKA_SERVER_URL:-http://localhost:8090/eureka/}
# Eureka实例主机名
export EUREKA_INSTANCE_HOSTNAME=${EUREKA_INSTANCE_HOSTNAME:-localhost}
# Eureka实例IP地址偏好
export EUREKA_INSTANCE_PREFER_IP=${EUREKA_INSTANCE_PREFER_IP:-true}
# ============================================================================
# JWT 配置
# ============================================================================
# JWT密钥（生产环境必须修改，建议使用至少256位的随机字符串）
export JWT_SECRET=${JWT_SECRET:-xuyou-secret-key-for-jwt-token-generation-minimum-256-bits-please-change-in-production}
# JWT过期时间（毫秒，默认24小时）
export JWT_EXPIRATION=${JWT_EXPIRATION:-86400000}
# ============================================================================
# 服务端口配置
# ============================================================================
# Eureka服务端口
export EUREKA_PORT=${EUREKA_PORT:-8090}
# User服务端口
export USER_PORT=${USER_PORT:-8092}
# ============================================================================
# 其他配置
# ============================================================================
# 服务器时区
export TZ=Asia/Shanghai
# 文件编码
export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8
# ============================================================================
# 输出配置信息（可选，用于调试）
# ============================================================================
# 如果需要查看加载的环境变量，取消下面的注释
# echo "=========================================="
# echo "环境变量配置已加载"
# echo "=========================================="
# echo "JAVA_HOME: $JAVA_HOME"
# echo "APP_HOME: $APP_HOME"
# echo "LOG_HOME: $LOG_HOME"
# echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
# echo "=========================================="