package ltd.xiaomizha.xuyou.common.utils.license;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.json.JSONUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * License 加密解密工具类
 * <p>
 * 用于处理 License 的加密、解密、签名和验证等操作
 */
public class LicenseCryptUtils {

    /**
     * 生成 RSA 密钥对
     *
     * @return 密钥对
     */
    public static KeyPair generateKeyPair() {
        return SecureUtil.generateKeyPair("RSA");
    }

    /**
     * 获取公钥的 Base64 编码
     *
     * @param publicKey 公钥
     * @return Base64 编码的公钥
     */
    public static String getPublicKeyBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 获取私钥的 Base64 编码
     *
     * @param privateKey 私钥
     * @return Base64 编码的私钥
     */
    public static String getPrivateKeyBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 从 Base64 编码恢复公钥
     *
     * @param publicKeyBase64 Base64 编码的公钥
     * @return 公钥
     */
    public static PublicKey getPublicKeyFromBase64(String publicKeyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        return SecureUtil.generatePublicKey("RSA", keyBytes);
    }

    /**
     * 从 Base64 编码恢复私钥
     *
     * @param privateKeyBase64 Base64 编码的私钥
     * @return 私钥
     */
    public static PrivateKey getPrivateKeyFromBase64(String privateKeyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        return SecureUtil.generatePrivateKey("RSA", keyBytes);
    }

    /**
     * 加密 License 数据
     *
     * @param data      待加密的数据
     * @param publicKey 公钥
     * @return 加密后的数据(Base64 编码)
     */
    public static String encryptLicense(String data, PublicKey publicKey) {
        if (StrUtil.isEmpty(data) || publicKey == null) {
            return null;
        }
        RSA rsa = new RSA(null, publicKey);
        return rsa.encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * 解密 License 数据
     *
     * @param encryptedData 加密的数据(Base64 编码)
     * @param privateKey    私钥
     * @return 解密后的数据
     */
    public static String decryptLicense(String encryptedData, PrivateKey privateKey) {
        if (StrUtil.isEmpty(encryptedData) || privateKey == null) {
            return null;
        }
        RSA rsa = new RSA(privateKey, null);
        return rsa.decryptStr(encryptedData, KeyType.PrivateKey);
    }

    /**
     * 签名 License 数据
     *
     * @param data       待签名的数据
     * @param privateKey 私钥
     * @return 签名(Base64 编码)
     */
    public static String signLicense(String data, PrivateKey privateKey) {
        if (StrUtil.isEmpty(data) || privateKey == null) {
            return null;
        }
        // RSA rsa = new RSA(privateKey, null);
        // byte[] signedBytes = rsa.encrypt(data, KeyType.PrivateKey);
        // return cn.hutool.core.codec.Base64.encode(signedBytes);
        Sign sign = new Sign(SignAlgorithm.SHA512withRSA, privateKey, null);
        return cn.hutool.core.codec.Base64.encode(sign.sign(data));
    }

    /**
     * 验证 License 签名
     *
     * @param data      原始数据
     * @param signature 签名(Base64 编码)
     * @param publicKey 公钥
     * @return 是否验证通过
     */
    public static boolean verifyLicense(String data, String signature, PublicKey publicKey) {
        if (StrUtil.isEmpty(data) || StrUtil.isEmpty(signature) || publicKey == null) {
            return false;
        }
        Sign sign = new Sign(SignAlgorithm.SHA512withRSA, null, publicKey);
        return sign.verify(data.getBytes(), signature.getBytes());
    }

    /**
     * 加密 License 对象
     *
     * @param licenseObject License 对象
     * @param publicKey     公钥
     * @return 加密后的字符串(Base64 编码)
     */
    public static String encryptLicenseObject(Object licenseObject, PublicKey publicKey) {
        if (licenseObject == null || publicKey == null) {
            return null;
        }
        String jsonString = JSONUtil.toJsonStr(licenseObject);
        return encryptLicense(jsonString, publicKey);
    }

    /**
     * 解密 License 对象
     *
     * @param encryptedData 加密的数据(Base64 编码)
     * @param privateKey    私钥
     * @param clazz         目标类
     * @param <T>           泛型
     * @return 解密后的对象
     */
    public static <T> T decryptLicenseObject(String encryptedData, PrivateKey privateKey, Class<T> clazz) {
        String jsonString = decryptLicense(encryptedData, privateKey);
        if (StrUtil.isEmpty(jsonString)) {
            return null;
        }
        return JSONUtil.toBean(jsonString, clazz);
    }

    /**
     * 生成 License 密钥对文件
     *
     * @param publicKeyPath  公钥文件路径
     * @param privateKeyPath 私钥文件路径
     * @throws RuntimeException 如果文件写入失败
     */
    public static void generateKeyPairFiles(String publicKeyPath, String privateKeyPath) {
        if (StrUtil.isEmpty(publicKeyPath) || StrUtil.isEmpty(privateKeyPath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        try {
            // 生成密钥对
            KeyPair keyPair = generateKeyPair();

            // 获取密钥的 Base64 编码
            String publicKeyBase64 = getPublicKeyBase64(keyPair.getPublic());
            String privateKeyBase64 = getPrivateKeyBase64(keyPair.getPrivate());

            // 确保目录存在
            FileUtil.mkParentDirs(publicKeyPath);
            FileUtil.mkParentDirs(privateKeyPath);

            // 写入文件
            FileUtil.writeUtf8String(publicKeyBase64, publicKeyPath);
            FileUtil.writeUtf8String(privateKeyBase64, privateKeyPath);
        } catch (Exception e) {
            throw new RuntimeException("生成密钥对文件失败: " + e.getMessage(), e);
        }
    }
}
