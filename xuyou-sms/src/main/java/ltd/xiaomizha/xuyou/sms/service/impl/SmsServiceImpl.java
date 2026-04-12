package ltd.xiaomizha.xuyou.sms.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.CacheConstant;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseBuilder;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.sms.dto.SmsDTO;
import ltd.xiaomizha.xuyou.sms.service.SmsService;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private final StringRedisTemplate stringRedisTemplate;
    private final SmsDTO smsDTO;

    public SmsServiceImpl(StringRedisTemplate stringRedisTemplate, SmsDTO smsDTO) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.smsDTO = smsDTO;
    }

    /**
     * 发送短信验证码
     * <p>
     * 生成6位随机验证码, 通过短信发送给用户,
     * 同时将验证码存入 Redis (默认5分钟过期)
     *
     * @param phone 手机号
     * @return 发送结果, 包含验证码
     */
    @Override
    public ResponseResult<Void> sendVerificationCode(String phone) {
        try {
            // 校验手机号格式
            if (phone == null || phone.trim().isEmpty() || !phone.matches("^1[3-9]\\d{9}$")) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "手机号格式不正确");
            }

            // 检查发送频率限制
            // 防止短信轰炸
            String intervalKey = CacheConstant.REDIS_PREFIX_SMS_CODE + "interval:" + phone;
            Boolean hasRecentSend = stringRedisTemplate.hasKey(intervalKey);
            if (hasRecentSend) {
                Long ttl = stringRedisTemplate.getExpire(intervalKey, TimeUnit.SECONDS);
                return ResponseBuilder.error(ResultEnum.TOO_MANY_REQUESTS, "操作过于频繁，请" + ttl + "秒后重试");
            }

            // 生成随机验证码
            String code = RandomUtil.randomNumbers(smsDTO.getCodeLength());

            // 获取短信实例并发送
            SmsBlend smsBlend = SmsFactory.getSmsBlend(smsDTO.getBlendName());
            SmsResponse response = smsBlend.sendMessage(phone, code);

            // 判断发送结果
            if (response.isSuccess()) {
                // 发送成功: 将验证码存入 Redis
                String codeKey = CacheConstant.REDIS_PREFIX_SMS_CODE + phone;
                stringRedisTemplate.opsForValue().set(codeKey, code, smsDTO.getCodeExpireSeconds(), TimeUnit.SECONDS);

                // 设置发送间隔标记
                stringRedisTemplate.opsForValue().set(intervalKey, "1", smsDTO.getSendIntervalSeconds(), TimeUnit.SECONDS);

                log.info("短信验证码发送成功: phone={}, code={}", phone, code);

                // 开发环境返回验证码
                // 生产环境隐藏
                return ResponseBuilder.success(code);
            } else {
                log.error("短信验证码发送失败: phone={}, error={}", phone, response.getData());
                return ResponseBuilder.error("短信发送失败: " + response.getData());
            }
        } catch (Exception e) {
            log.error("发送短信验证码异常: phone={}", phone, e);
            return ResponseBuilder.error("发送短信验证码失败: " + e.getMessage());
        }
    }

    /**
     * 校验短信验证码
     * <p>
     * 从 Redis 获取存储的验证码, 与用户输入的验证码进行比对
     *
     * @param phone 手机号
     * @param code  用户输入的验证码
     * @return 校验结果
     */
    @Override
    public ResponseResult<Void> verifyCode(String phone, String code) {
        try {
            // 参数校验
            if (phone == null || code == null) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "手机号和验证码不能为空");
            }

            // 从 Redis 获取存储的验证码
            String codeKey = CacheConstant.REDIS_PREFIX_SMS_CODE + phone;
            String storedCode = stringRedisTemplate.opsForValue().get(codeKey);

            if (storedCode == null) {
                log.warn("验证码不存在或已过期: phone={}", phone);
                return ResponseBuilder.error("验证码不存在或已过期，请重新获取");
            }

            // 对比验证码 (忽略大小写)
            if (!storedCode.equalsIgnoreCase(code)) {
                log.warn("验证码错误: phone={}, input={}, stored={}", phone, code, storedCode);
                return ResponseBuilder.error("验证码错误");
            }

            // 校验成功, 删除 Redis 中的验证码 (一次性使用)
            stringRedisTemplate.delete(codeKey);

            log.info("验证码校验成功: phone={}", phone);
            return ResponseBuilder.success();
        } catch (Exception e) {
            log.error("校验短信验证码异常: phone={}", phone, e);
            return ResponseBuilder.error("校验验证码失败: " + e.getMessage());
        }
    }

    /**
     * 发送自定义短信内容
     *
     * @param phone   手机号
     * @param content 短信内容 (需符合模板变量)
     * @param args    模板参数 (键值对形式)
     * @return 发送结果
     */
    @Override
    public ResponseResult<Void> sendCustomMessage(String phone, String content, String... args) {
        try {
            // 参数校验
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "手机号不能为空");
            }

            // 获取短信实例
            SmsBlend smsBlend = SmsFactory.getSmsBlend(smsDTO.getBlendName());

            // 发送短信
            SmsResponse response;
            if (args != null && args.length > 0) {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                for (int i = 0; i < args.length; i++) {
                    params.put("param" + (i + 1), args[i]);
                }
                response = smsBlend.sendMessage(phone, content, params);
            } else {
                response = smsBlend.sendMessage(phone, content);
            }

            // 返回结果
            if (response.isSuccess()) {
                log.info("自定义短信发送成功: phone={}", phone);
                return ResponseBuilder.success();
            } else {
                log.error("自定义短信发送失败: phone={}, error={}", phone, response.getData());
                return ResponseBuilder.error("短信发送失败: " + response.getData());
            }
        } catch (Exception e) {
            log.error("发送自定义短信异常: phone={}", phone, e);
            return ResponseBuilder.error("发送短信失败: " + e.getMessage());
        }
    }
}
