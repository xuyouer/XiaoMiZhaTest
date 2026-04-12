package ltd.xiaomizha.xuyou.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.auth.service.ScanLoginService;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("auth/scan/login")
@Tag(name = "扫码登录管理", description = "扫码登录API (基于二维码的移动端认证)")
public class ScanLoginController {

    @Resource
    private ScanLoginService scanLoginService;

    /**
     * 生成登录二维码 (前端调用, 获取二维码和临时凭证)
     *
     * @return 二维码 Base64 + 临时凭证 uuid + 过期时间
     */
    @GetMapping("/qrcode")
    @Operation(summary = "生成登录二维码", description = "生成扫码登录二维码图片和临时凭证")
    public ResponseResult<?> generateLoginQrCode() {
        try {
            Map<String, Object> data = scanLoginService.generateLoginQrCode();
            return ResponseResult.ok(data);
        } catch (Exception e) {
            log.error("生成登录二维码失败", e);
            return ResponseResult.error("生成登录二维码失败: " + e.getMessage());
        }
    }

    /**
     * 查询扫码状态 (前端轮询调用, 间隔 1-2 秒)
     *
     * @param uuid 临时凭证
     * @return 扫码状态 + 登录 Token, 确认登录后返回
     */
    @GetMapping("/state")
    @Operation(summary = "查询扫码状态", description = "前端轮询查询二维码扫码状态 (未扫码/已扫码待确认/已确认登录/已过期)")
    public ResponseResult<?> queryScanState(@RequestParam String uuid) {
        try {
            Map<String, Object> data = scanLoginService.queryScanState(uuid);
            return ResponseResult.ok(data);
        } catch (IllegalArgumentException e) {
            log.warn("参数错误: {}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询扫码状态失败: uuid={}", uuid, e);
            return ResponseResult.error("查询扫码状态失败: " + e.getMessage());
        }
    }

    /**
     * 扫码确认登录 (手机端调用)
     *
     * @param uuid   临时凭证 (手机端解析二维码获取)
     * @param userId 手机端当前登录用户 ID (从手机端 Token 解析)
     * @return 确认结果
     */
    @PostMapping("/confirm")
    @Operation(summary = "扫码确认登录", description = "手机端扫码后调用此接口确认登录, 触发 PC 端完成登录流程")
    public ResponseResult<?> confirmLogin(
            @RequestParam String uuid,
            @RequestParam Long userId) {
        try {
            String result = scanLoginService.confirmLogin(uuid, userId);
            return ResponseResult.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数错误: {}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("业务逻辑错误: {}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("扫码确认登录失败: uuid={}, userId={}", uuid, userId, e);
            return ResponseResult.error("扫码确认登录失败: " + e.getMessage());
        }
    }

    /**
     * 标记已扫码 (手机端扫描二维码后调用, 未确认前)
     *
     * @param uuid 临时凭证
     * @return 操作结果
     */
    @PostMapping("/scanned")
    @Operation(summary = "标记已扫码", description = "手机端扫描二维码后调用 (未确认前), 用于更新状态为'已扫码待确认'")
    public ResponseResult<?> markScanned(@RequestParam String uuid) {
        try {
            String result = scanLoginService.markScanned(uuid);
            return ResponseResult.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数错误: {}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("业务逻辑错误: {}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("标记已扫码失败: uuid={}", uuid, e);
            return ResponseResult.error("操作失败: " + e.getMessage());
        }
    }

}
