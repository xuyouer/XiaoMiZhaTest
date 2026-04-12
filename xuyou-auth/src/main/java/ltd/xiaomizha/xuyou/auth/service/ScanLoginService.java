package ltd.xiaomizha.xuyou.auth.service;

import java.util.Map;

/**
 * 扫码登录服务
 */
public interface ScanLoginService {

    /**
     * 生成登录二维码
     *
     * @return 包含二维码 Base64、UUID、过期时间的 Map
     */
    Map<String, Object> generateLoginQrCode();

    /**
     * 查询扫码状态
     *
     * @param uuid 临时凭证
     * @return 包含状态码、描述、Token 信息 (确认登录后) 的 Map
     */
    Map<String, Object> queryScanState(String uuid);

    /**
     * 扫码确认登录 (手机端调用)
     *
     * @param uuid   临时凭证
     * @param userId 手机端当前登录用户ID
     * @return 操作结果消息
     */
    String confirmLogin(String uuid, Long userId);

    /**
     * 标记已扫码 (手机端扫描后调用, 未确认前)
     *
     * @param uuid 临时凭证
     * @return 操作结果消息
     */
    String markScanned(String uuid);

}
