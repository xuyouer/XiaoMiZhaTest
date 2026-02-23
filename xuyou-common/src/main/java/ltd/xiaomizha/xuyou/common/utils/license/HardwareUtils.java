package ltd.xiaomizha.xuyou.common.utils.license;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.DigestUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 硬件信息工具类
 * <p>
 * 用于获取硬件信息和生成激活码
 */
public class HardwareUtils {

    // 时间误差常量(5分钟, 单位: 毫秒)
    private static final long TIME_ERROR_TOLERANCE = 5 * 60 * 1000;

    // 激活码格式常量
    private static final int ACTIVATION_CODE_SEGMENT_LENGTH = 9;

    /**
     * 获取硬件信息并返回MD5加密结果
     *
     * @return MD5加密后的硬件信息
     */
    public static String getHardwareInfo() {
        StringBuilder hardwareInfo = new StringBuilder();

        // 获取MAC地址
        String macAddress = getHardwareProperty("ipconfig /all", "Physical Address[\\. ]*: ([0-9A-F-]+)", null);
        hardwareInfo.append("MAC:").append(macAddress.replaceAll("-", "").toUpperCase()).append("|");

        // 获取CPU序列号
        String cpuSerial = getHardwareProperty("wmic cpu get ProcessorId", null, "ProcessorId");
        hardwareInfo.append("CPU_SERIAL:").append(cpuSerial).append("|");

        // 获取CPU详细信息
        String cpuInfo = getHardwareProperty("wmic cpu get Name", null, "Name");
        hardwareInfo.append("CPU_INFO:").append(cpuInfo).append("|");

        // 获取内存信息
        String memoryInfo = getHardwareProperty("wmic memorychip get Capacity", null, "Capacity");
        hardwareInfo.append("MEMORY:").append(memoryInfo).append("|");

        // 获取硬盘序列号
        String diskSerial = getHardwareProperty("wmic diskdrive get SerialNumber", null, "SerialNumber");
        hardwareInfo.append("DISK:").append(diskSerial);

        // 使用MD5加密
        // return hardwareInfo.toString();
        return DigestUtil.md5Hex(hardwareInfo.toString());
    }

    /**
     * 获取硬件属性的通用方法
     *
     * @param command      执行的命令
     * @param patternRegex 正则表达式(用于匹配特定格式的输出)
     * @param header       表头名称(用于跳过表头)
     * @return 硬件属性值
     */
    private static String getHardwareProperty(String command, String patternRegex, String header) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = patternRegex != null ? Pattern.compile(patternRegex) : null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // 跳过表头
                if (header != null && header.equals(line)) {
                    continue;
                }

                // 使用正则表达式匹配
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        reader.close();
                        return matcher.group(1);
                    }
                } else {
                    // 直接返回非空行
                    reader.close();
                    return line;
                }
            }
            reader.close();
        } catch (IOException e) {
            // 静默处理异常, 返回unknown
        }
        return "unknown";
    }

    /**
     * 生成激活码
     *
     * @param hardwareInfo 硬件信息
     * @param expireDays   过期天数
     * @return 激活码
     */
    public static String generateActivationCode(String hardwareInfo, int expireDays) {
        try {
            // 生成RSA密钥对
            KeyPair keyPair = LicenseCryptUtils.generateKeyPair();
            RSA rsa = new RSA(keyPair.getPrivate(), keyPair.getPublic());

            // 构建激活码内容
            String userInfo = System.getProperty("user.name");
            String osInfo = System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch");
            String javaVersion = System.getProperty("java.version");
            long randomNumber = System.nanoTime();
            // String activationInfo = String.format("%s|%d|%d", hardwareInfo, expireDays, System.currentTimeMillis());
            String activationInfo = String.format("%s|%d|%d|%s|%s|%s|%d",
                    hardwareInfo, expireDays, System.currentTimeMillis(),
                    userInfo,
                    osInfo, javaVersion, randomNumber);

            // 加密
            String encrypted = rsa.encryptBase64(activationInfo, KeyType.PublicKey);

            // 生成激活码(使用Base64编码并进行格式化)
            String activationCode = Base64.encode(encrypted.getBytes());

            // 格式化激活码
            return formatActivationCode(activationCode);
        } catch (Exception e) {
            // 静默处理异常, 返回null
            return null;
        }
    }

    /**
     * 格式化激活码
     *
     * @param code 原始激活码
     * @return 格式化后的激活码
     */
    private static String formatActivationCode(String code) {
        StringBuilder formattedCode = new StringBuilder();
        for (int i = 0; i < code.length(); i += ACTIVATION_CODE_SEGMENT_LENGTH) {
            if (i > 0) {
                formattedCode.append("-");
            }
            formattedCode.append(code.substring(i, Math.min(i + ACTIVATION_CODE_SEGMENT_LENGTH, code.length())));
        }
        return formattedCode.toString();
    }

    /**
     * 验证激活码
     *
     * @param activationCode 激活码
     * @param publicKey      公钥
     * @return 激活码是否有效
     */
    public static boolean validateActivationCode(String activationCode, String publicKey) {
        try {
            // 移除连字符
            String code = activationCode.replaceAll("-", "");

            // 解码Base64
            byte[] decoded = Base64.decode(code);
            String encrypted = new String(decoded);

            // 使用公钥解密
            RSA rsa = new RSA(null, LicenseCryptUtils.getPublicKeyFromBase64(publicKey));
            String decrypted = rsa.decryptStr(encrypted, KeyType.PublicKey);

            // 解析激活码中的信息
            String[] parts = decrypted.split("\\|");
            if (parts.length != 6) {
                return false;
            }

            String storedHardwareInfo = parts[0];
            int expireDays;
            long timestamp;

            try {
                expireDays = Integer.parseInt(parts[1]);
                timestamp = Long.parseLong(parts[2]);
            } catch (NumberFormatException e) {
                // 数字格式错误, 激活码无效
                return false;
            }

            // 验证硬件信息是否匹配
            String currentHardwareInfo = getHardwareInfo();
            if (!storedHardwareInfo.equals(currentHardwareInfo)) {
                return false;
            }

            // 验证激活码是否过期
            long now = System.currentTimeMillis();
            long expireTime = timestamp + (long) expireDays * 24 * 60 * 60 * 1000;
            if (now > expireTime) {
                return false;
            }

            // 验证时间戳是否有效(防止重放攻击)
            long timeDiff = Math.abs(now - timestamp);
            if (timeDiff > TIME_ERROR_TOLERANCE) {
                return false;
            }

            return true;
        } catch (Exception e) {
            // 静默处理异常, 返回false
            return false;
        }
    }
}
