package ltd.xiaomizha.xuyou.common.config;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class CustomIdGenerator implements IdentifierGenerator {

    private final AtomicLong atomicLong = new AtomicLong(1);
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public CustomIdGenerator() {
        // 通过配置获取工作ID和数据中心ID
        long workerId = getWorkerIdFromConfig();
        long datacenterId = getDatacenterIdFromConfig();
        this.snowflakeIdGenerator = new SnowflakeIdGenerator(workerId, datacenterId);
    }

    public CustomIdGenerator(long workerId, long datacenterId) {
        this.snowflakeIdGenerator = new SnowflakeIdGenerator(workerId, datacenterId);
    }

    @Override
    public Number nextId(Object entity) {
        try {
            return snowflakeIdGenerator.nextId();
        } catch (Exception e) {
            log.error("Snowflake ID generation failed, falling back to atomic counter", e);
            // 使用原子递增
            return atomicLong.getAndIncrement();
        }
    }

    @Override
    public String nextUUID(Object entity) {
        // return IdentifierGenerator.super.nextUUID(entity);
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 雪花ID生成器
     * <p>
     * START_TIMESTAMP起始时间戳, 用于减少生成ID的长度(相对于1970-01-01)
     * <p>
     * 41位时间戳可以表示的时间范围是2^41毫秒, 约69年
     * <p>
     * 例如从2021-01-01开始, 可以支持到2021+69=2090年左右
     * <p>
     * 减少ID长度, 提高存储和传输效率
     * <p>
     */
    private static class SnowflakeIdGenerator {
        // 起始时间戳, 作为项目的开始时间 2021-01-01 00:00:00
        private static final long START_TIMESTAMP = 1609459200000L;
        // 各部分的位数
        private static final long WORKER_ID_BITS = 5L;     // 5位工作机器ID
        private static final long DATACENTER_ID_BITS = 5L; // 5位数据中心ID
        private static final long SEQUENCE_BITS = 12L;     // 12位序列号

        // 最大值
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

        // 偏移量
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

        // 实例变量
        private final long workerId;
        private final long datacenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        /**
         * 构造函数
         *
         * @param workerId     工作机器ID (0-31)
         * @param datacenterId 数据中心ID (0-31)
         */
        public SnowflakeIdGenerator(long workerId, long datacenterId) {
            if (workerId > MAX_WORKER_ID || workerId < 0) {
                throw new IllegalArgumentException(
                        String.format("workerId must be between 0 and %d", MAX_WORKER_ID));
            }
            if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
                throw new IllegalArgumentException(
                        String.format("datacenterId must be between 0 and %d", MAX_DATACENTER_ID));
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
        }


        /**
         * 生成下一个ID
         */
        public synchronized long nextId() {
            long timestamp = timeGen();

            // 时钟回拨处理
            if (timestamp < lastTimestamp) {
                long offset = lastTimestamp - timestamp;
                if (offset <= 5) {
                    // 时钟回拨在5ms内, 等待
                    timestamp = waitUntilNextMillis(lastTimestamp);
                } else {
                    // 时钟回拨超过5ms, 抛出异常
                    throw new RuntimeException(
                            String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset));
                }
            }

            // 同一毫秒内生成
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    // 序列号用尽, 等待下一毫秒
                    timestamp = waitUntilNextMillis(lastTimestamp);
                }
            } else {
                // 不同毫秒, 序列号重置
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            // 组合各部分生成最终ID
            return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                    | (datacenterId << DATACENTER_ID_SHIFT)
                    | (workerId << WORKER_ID_SHIFT)
                    | sequence;
        }

        /**
         * 等待直到下一毫秒
         */
        private long waitUntilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        /**
         * 获取当前时间戳（毫秒）
         */
        private long timeGen() {
            return System.currentTimeMillis();
        }

        /**
         * 解析雪花ID
         * 用于调试和分析生成的ID
         */
        public SnowflakeInfo parseId(long id) {
            long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
            long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
            long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
            long sequence = id & SEQUENCE_MASK;

            return new SnowflakeInfo(timestamp, datacenterId, workerId, sequence);
        }
    }

    /**
     * 雪花ID解析结果
     */
    @Getter
    @ToString
    public static class SnowflakeInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private final long timestamp;
        private final long datacenterId;
        private final long workerId;
        private final long sequence;

        public SnowflakeInfo(long timestamp, long datacenterId, long workerId, long sequence) {
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        /**
         * 获取生成时间
         */
        public Date getGenerateTime() {
            return new Date(timestamp);
        }
    }

    /**
     * 解析雪花ID
     * <p>
     * 雪花ID结构：
     * <p>
     * 1. 41位时间戳（从START_TIMESTAMP开始）
     * <p>
     * 2. 5位数据中心ID
     * <p>
     * 3. 5位工作机器ID
     * <p>
     * 4. 12位序列号
     * <p>
     */
    public static SnowflakeInfo analyzeId(long id) {
        // SnowflakeIdGenerator generator = new SnowflakeIdGenerator(0, 0);
        // return generator.parseId(id);

        // 定义常量（与SnowflakeIdGenerator中保持一致）
        final long START_TIMESTAMP = 1609459200000L;
        final long WORKER_ID_BITS = 5L;
        final long DATACENTER_ID_BITS = 5L;
        final long SEQUENCE_BITS = 12L;

        final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
        final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

        final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

        // 解析各部分
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence = id & SEQUENCE_MASK;
        return new SnowflakeInfo(timestamp, datacenterId, workerId, sequence);
    }

    /**
     * 从配置获取工作机器ID
     * 可以通过系统属性、环境变量或配置文件获取
     */
    private long getWorkerIdFromConfig() {
        // 1. 优先从系统属性获取
        String workerIdStr = System.getProperty("snowflake.worker.id");
        if (workerIdStr != null && !workerIdStr.isEmpty()) {
            try {
                return Long.parseLong(workerIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid workerId from system property: {}", workerIdStr);
            }
        }

        // 2. 从环境变量获取
        workerIdStr = System.getenv("SNOWFLAKE_WORKER_ID");
        if (workerIdStr != null && !workerIdStr.isEmpty()) {
            try {
                return Long.parseLong(workerIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid workerId from environment variable: {}", workerIdStr);
            }
        }

        // 3. 使用默认值
        return generateDefaultWorkerId();
    }

    /**
     * 从配置获取数据中心ID
     */
    private long getDatacenterIdFromConfig() {
        // 1. 优先从系统属性获取
        String datacenterIdStr = System.getProperty("snowflake.datacenter.id");
        if (datacenterIdStr != null && !datacenterIdStr.isEmpty()) {
            try {
                return Long.parseLong(datacenterIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid datacenterId from system property: {}", datacenterIdStr);
            }
        }

        // 2. 从环境变量获取
        datacenterIdStr = System.getenv("SNOWFLAKE_DATACENTER_ID");
        if (datacenterIdStr != null && !datacenterIdStr.isEmpty()) {
            try {
                return Long.parseLong(datacenterIdStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid datacenterId from environment variable: {}", datacenterIdStr);
            }
        }

        // 3. 使用默认值
        return 0L;
    }

    /**
     * 生成默认的工作机器ID（基于主机名或IP）
     */
    private long generateDefaultWorkerId() {
        try {
            // 方法1: 基于主机名的哈希值
            String hostname = getHostName();
            if (hostname != null && !hostname.isEmpty()) {
                int hashCode = Math.abs(hostname.hashCode());
                return hashCode % 32; // 在0-31范围内
            }

            // 方法2: 基于IP地址的最后一段
            String ip = getLocalIp();
            if (ip != null && !ip.isEmpty()) {
                String[] segments = ip.split("\\.");
                if (segments.length == 4) {
                    int lastSegment = Integer.parseInt(segments[3]);
                    return lastSegment % 32;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to generate default workerId", e);
        }

        // 方法3: 使用随机数
        return (long) (Math.random() * 32);
    }

    /**
     * 获取主机名
     */
    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取本地IP
     */
    private String getLocalIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析ID并打印信息
     */
    public static void printIdInfo(long id) {
        SnowflakeInfo info = analyzeId(id);
        log.info("=== Snowflake ID 解析结果 ===");
        log.info("原始ID: {}", id);
        log.info("生成时间: {}", info.getGenerateTime());
        log.info("数据中心ID: {}", info.getDatacenterId());
        log.info("工作机器ID: {}", info.getWorkerId());
        log.info("序列号: {}", info.getSequence());
        log.info("=============================");
    }

    /**
     * 获取当前配置信息
     */
    public String getConfigInfo() {
        return String.format("Snowflake Config: workerId=%d, datacenterId=%d",
                snowflakeIdGenerator.workerId, snowflakeIdGenerator.datacenterId);
    }
}