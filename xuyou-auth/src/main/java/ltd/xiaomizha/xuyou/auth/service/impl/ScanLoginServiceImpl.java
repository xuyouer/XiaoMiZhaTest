package ltd.xiaomizha.xuyou.auth.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.auth.dto.AuthResponseDTO;
import ltd.xiaomizha.xuyou.auth.service.ScanLoginService;
import ltd.xiaomizha.xuyou.auth.service.TokenService;
import ltd.xiaomizha.xuyou.common.constant.CacheConstant;
import ltd.xiaomizha.xuyou.common.enums.scan.ScanLoginStatus;
import ltd.xiaomizha.xuyou.common.utils.qrcode.QrCodeUtil;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScanLoginServiceImpl implements ScanLoginService {

    @Resource
    private QrCodeUtil qrCodeUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TokenService tokenService;

    @Resource
    private UsersService usersService;

    /**
     * 生成登录二维码
     *
     * @return 包含二维码 Base64、UUID、过期时间的 Map
     */
    @Override
    public Map<String, Object> generateLoginQrCode() {
        // 生成唯一临时凭证 (uuid), 用于关联二维码和扫码状态
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 二维码内容: 拼接完整的回调URL, 方便手机端扫码后直接跳转到确认页面
        // 格式: {base-url}{confirm-path}?uuid={uuid}
        String qrCodeContent = qrCodeUtil.getScanLoginCallbackBaseUrl() + qrCodeUtil.getScanLoginConfirmPath() + "?uuid=" + uuid;

        // 生成二维码 Base64
        // String qrCodeBase64 = qrCodeUtil.generateQrCodeBase64(qrCodeContent);
        String qrCodeBase64 = qrCodeUtil.generateQrCodeByConfig(qrCodeContent);

        // 存储二维码状态到 Redis, 初始状态: 未扫码, 设置过期时间
        String redisKey = CacheConstant.REDIS_PREFIX_SCAN_LOGIN + uuid;
        stringRedisTemplate.opsForValue().set(
                redisKey,
                ScanLoginStatus.NOT_SCAN.getCode().toString(),
                qrCodeUtil.getExpireTime(),
                TimeUnit.SECONDS
        );

        log.info("生成扫码登录二维码: uuid={}, 过期时间={}秒", uuid, qrCodeUtil.getExpireTime());

        Map<String, Object> data = new HashMap<>();
        data.put("qrCodeBase64", qrCodeBase64);
        data.put("uuid", uuid);
        data.put("expireTime", qrCodeUtil.getExpireTime());

        return data;
    }

    /**
     * 查询扫码状态
     *
     * @param uuid 临时凭证
     * @return 包含状态码、描述、Token 信息 (确认登录后) 的 Map
     */
    @Override
    public Map<String, Object> queryScanState(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new IllegalArgumentException("临时凭证不能为空");
        }

        String redisKey = CacheConstant.REDIS_PREFIX_SCAN_LOGIN + uuid;
        String statusCode = stringRedisTemplate.opsForValue().get(redisKey);

        // 二维码已过期 (Redis 中无数据)
        if (StringUtils.isBlank(statusCode)) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", ScanLoginStatus.EXPIRED.getCode());
            data.put("desc", ScanLoginStatus.EXPIRED.getDesc());
            return data;
        }

        ScanLoginStatus status = ScanLoginStatus.getByCode(Integer.parseInt(statusCode));
        Map<String, Object> data = new HashMap<>();
        data.put("status", status.getCode());
        data.put("desc", status.getDesc());

        // 已确认登录, 生成登录 Token
        if (status == ScanLoginStatus.CONFIRMED) {
            generateLoginToken(data, redisKey, uuid);
        }

        return data;
    }

    /**
     * 扫码确认登录 (手机端调用)
     *
     * @param uuid   临时凭证
     * @param userId 手机端当前登录用户ID
     * @return 操作结果消息
     */
    @Override
    public String confirmLogin(String uuid, Long userId) {
        if (StringUtils.isBlank(uuid) || userId == null) {
            throw new IllegalArgumentException("参数不完整");
        }

        String redisKey = CacheConstant.REDIS_PREFIX_SCAN_LOGIN + uuid;
        String statusCode = stringRedisTemplate.opsForValue().get(redisKey);

        // 二维码已过期或不存在
        if (StringUtils.isBlank(statusCode)) {
            throw new IllegalStateException("二维码已过期, 请重新生成");
        }

        ScanLoginStatus status = ScanLoginStatus.getByCode(Integer.parseInt(statusCode));

        // 只有未扫码 / 已扫码待确认状态, 才能确认登录
        if (status == ScanLoginStatus.NOT_SCAN || status == ScanLoginStatus.SCANNED) {
            // 更新状态为已确认登录
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    ScanLoginStatus.CONFIRMED.getCode().toString()
            );

            // 存储用户 ID (用于后续生成 Token)
            stringRedisTemplate.opsForValue().set(
                    redisKey + CacheConstant.REDIS_PREFIX_SCAN_LOGIN_USER_ID,
                    String.valueOf(userId),
                    qrCodeUtil.getExpireTime(),
                    TimeUnit.SECONDS
            );

            log.info("用户扫码确认登录: userId={}, uuid={}", userId, uuid);
            return "确认登录成功";
        }

        throw new IllegalStateException("当前状态无法确认登录 (" + status.getDesc() + ")");
    }

    /**
     * 标记已扫码 (手机端扫描后调用, 未确认前)
     *
     * @param uuid 临时凭证
     * @return 操作结果消息
     */
    @Override
    public String markScanned(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new IllegalArgumentException("临时凭证不能为空");
        }

        String redisKey = CacheConstant.REDIS_PREFIX_SCAN_LOGIN + uuid;
        String statusCode = stringRedisTemplate.opsForValue().get(redisKey);

        if (StringUtils.isBlank(statusCode)) {
            throw new IllegalStateException("二维码已过期或不存在");
        }

        ScanLoginStatus status = ScanLoginStatus.getByCode(Integer.parseInt(statusCode));

        // 只有未扫码状态才能标记为已扫码
        if (status == ScanLoginStatus.NOT_SCAN) {
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    ScanLoginStatus.SCANNED.getCode().toString(),
                    qrCodeUtil.getExpireTime(),
                    TimeUnit.SECONDS
            );
            log.info("二维码已被扫描: uuid={}", uuid);
            return "已标记为已扫码";
        }

        return "当前状态: " + status.getDesc();
    }

    /**
     * 生成登录Token, 需确认登录
     *
     * @param data     返回数据 Map
     * @param redisKey Redis key
     * @param uuid     临时凭证
     */
    private void generateLoginToken(Map<String, Object> data, String redisKey, String uuid) {
        // 从 Redis 获取用户 ID (确认时存入)
        String userIdStr = stringRedisTemplate.opsForValue().get(redisKey + CacheConstant.REDIS_PREFIX_SCAN_LOGIN_USER_ID);
        if (StringUtils.isNotBlank(userIdStr)) {
            Long userId = Long.valueOf(userIdStr);

            // 查询用户信息
            Users user = usersService.getById(userId);
            if (user != null) {
                UserDetailDTO userDetail = usersService.getUserDetailById(userId.intValue());

                // 使用默认认证模式生成 Token
                AuthResponseDTO authResponse = tokenService.login(
                        userId.intValue(),
                        user.getUsername(),
                        userDetail,
                        null
                );

                data.put("tokenInfo", authResponse);

                log.info("扫码登录成功: userId={}, uuid={}", userId, uuid);
            }
        }

        // 登录成功后, 删除 Redis 中的临时状态, 避免重复登录
        stringRedisTemplate.delete(redisKey);
        stringRedisTemplate.delete(redisKey + CacheConstant.REDIS_PREFIX_SCAN_LOGIN_USER_ID);
    }
}
