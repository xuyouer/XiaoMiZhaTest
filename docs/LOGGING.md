# 日志配置说明

## 概述

项目使用 Logback 作为日志框架，通过 `logback-spring.xml` 配置文件实现标准化的日志管理。

## 日志文件结构

### 日志文件位置

- 默认路径：`logs/` 目录（可通过环境变量 `LOG_HOME` 配置）
- 日志文件命名：`{应用名称}-{类型}.log`

### 日志文件类型

1. **全量日志** (`{app-name}-all.log`)
    - 记录所有级别的日志（DEBUG、INFO、WARN、ERROR）
    - 按日期和大小滚动
    - 单个文件最大：100MB
    - 保留天数：30天
    - 总大小限制：10GB
2. **错误日志** (`{app-name}-error.log`)
    - 仅记录 ERROR 级别日志
    - 单个文件最大：50MB
    - 保留天数：60天
    - 总大小限制：5GB
3. **警告日志** (`{app-name}-warn.log`)
    - 记录 WARN 和 ERROR 级别日志
    - 单个文件最大：50MB
    - 保留天数：30天
    - 总大小限制：2GB

## 日志格式

### 控制台输出格式

```
2026-01-28 09:15:22.123 [main] INFO  ltd.xiaomizha.xuyou.Application - 应用启动成功
```

- 彩色输出（开发环境）
- 包含时间戳、线程名、日志级别、类名、消息

### 文件输出格式

```
2026-01-28 09:15:22.123 [main] INFO  ltd.xiaomizha.xuyou.Application - 应用启动成功
```

- 纯文本格式
- 便于日志分析工具处理

## 环境配置

### 开发环境 (dev/development)

- 控制台输出：启用（彩色）
- 文件输出：启用
- 日志级别：
    - 项目包：DEBUG
    - Spring框架：INFO
    - MyBatis Plus：DEBUG
    - SQL日志：DEBUG

### 测试环境 (test)

- 控制台输出：启用
- 文件输出：启用
- 日志级别：
    - 项目包：INFO
    - Spring框架：WARN
    - MyBatis Plus：INFO

### 生产环境 (prod/production)

- 控制台输出：禁用（仅文件输出）
- 文件输出：启用
- 日志级别：
    - 项目包：INFO
    - Spring框架：WARN
    - MyBatis Plus：WARN
    - 第三方框架：WARN

## 环境变量配置

可以通过环境变量自定义日志配置：

```bash
# 日志文件存储路径
export LOG_HOME=/var/log/xuyou
# 日志级别
export LOG_LEVEL_APP=DEBUG
export LOG_LEVEL_SPRING=INFO
export LOG_LEVEL_MYBATIS=DEBUG
export LOG_LEVEL_ROOT=INFO
# 日志文件保留策略
export LOG_MAX_HISTORY=60        # 保留天数
export LOG_MAX_FILE_SIZE=200MB   # 单个文件最大大小
export LOG_TOTAL_SIZE_CAP=20GB    # 总大小限制
```

## 日志滚动策略

### 滚动规则

- **按日期滚动**：每天生成新的日志文件
- **按大小滚动**：单个文件达到最大大小时滚动
- **文件命名**：`{app-name}-{type}.{日期}.{序号}.log`

### 示例

```
xuyou-all.log                    # 当前日志文件
xuyou-all.2026-01-27.0.log      # 昨天的日志
xuyou-all.2026-01-27.1.log      # 昨天第二个文件
xuyou-all.2026-01-26.0.log      # 前天的日志
```

## 性能优化

### 异步输出

- 所有文件输出都使用异步Appender
- 队列大小：512（全量日志）、256（错误/警告日志）
- 不丢失日志：队列满时阻塞，不丢弃

### 建议

- 生产环境建议关闭控制台输出
- 使用异步输出提升性能
- 定期清理过期日志文件

## 日志级别说明

- **DEBUG**：详细的调试信息，通常只在开发时使用
- **INFO**：一般信息，记录程序运行的关键步骤
- **WARN**：警告信息，程序可以继续运行，但可能存在潜在问题
- **ERROR**：错误信息，程序出现错误但可以继续运行
- **FATAL**：严重错误，可能导致程序无法继续运行

## 日志查看

### 实时查看日志

```bash
# 查看全量日志
tail -f logs/xuyou-all.log
# 查看错误日志
tail -f logs/xuyou-error.log
# 查看警告日志
tail -f logs/xuyou-warn.log
```

### 日志搜索

```bash
# 搜索包含特定关键字的日志
grep "ERROR" logs/xuyou-all.log
# 搜索特定时间段的日志
grep "2026-01-28 09:" logs/xuyou-all.log
# 统计错误数量
grep -c "ERROR" logs/xuyou-error.log
```

## 日志分析工具推荐

1. **ELK Stack** (Elasticsearch + Logstash + Kibana)
2. **Grafana Loki**
3. **Splunk**
4. **阿里云日志服务**

## 注意事项

1. **日志文件权限**：确保应用有权限在日志目录创建和写入文件
2. **磁盘空间**：定期检查日志文件占用空间，避免磁盘满
3. **敏感信息**：不要在日志中输出密码、token等敏感信息
4. **日志级别**：生产环境建议使用INFO级别，避免过多DEBUG日志
5. **日志格式**：保持统一的日志格式，便于后续分析

## 故障排查

### 日志文件未生成

- 检查 `LOG_HOME` 环境变量或 `logs/` 目录是否存在
- 检查应用是否有写入权限
- 检查 logback-spring.xml 配置是否正确

### 日志文件过大

- 检查滚动策略配置
- 检查日志级别是否设置过低（如DEBUG）
- 考虑调整 `max-file-size` 和 `max-history`

### 性能问题

- 检查是否使用了异步Appender
- 检查日志级别，避免过多DEBUG日志
- 考虑使用日志聚合工具