package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.VipLevelConfig;
import ltd.xiaomizha.xuyou.user.service.VipLevelConfigService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("vip-level-config")
@Tag(name = "VIP等级配置管理", description = "VIP等级配置管理API")
public class VipLevelConfigController {

    @Resource
    private VipLevelConfigService vipLevelConfigService;

    @Operation(summary = "获取VIP等级配置列表", description = "分页获取VIP等级配置列表")
    @GetMapping("/list")
    public ResponseResult<?> getList(@RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<VipLevelConfig> page = vipLevelConfigService.page(
                    new Page<>(current, pageSize));
            return ResponseResultPage.ok(page.getRecords(), page.getCurrent(), page.getSize(), page.getTotal());
        } catch (Exception e) {
            log.error("获取VIP等级配置列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取VIP等级配置详情", description = "根据等级ID获取详情")
    @GetMapping("/{levelId}")
    public ResponseResult<?> getById(@Parameter(description = "等级ID") @PathVariable Integer levelId) {
        try {
            VipLevelConfig config = vipLevelConfigService.getById(levelId);
            return config != null ? ResponseResult.success(config) : ResponseResult.error("配置不存在");
        } catch (Exception e) {
            log.error("获取VIP等级配置详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增VIP等级配置")
    @PostMapping
    public ResponseResult<?> add(@RequestBody VipLevelConfig vipLevelConfig) {
        try {
            boolean result = vipLevelConfigService.save(vipLevelConfig);
            return result ? ResponseResult.success() : ResponseResult.error("新增失败");
        } catch (Exception e) {
            log.error("新增VIP等级配置失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新VIP等级配置")
    @PutMapping("/{levelId}")
    public ResponseResult<?> update(@Parameter(description = "等级ID") @PathVariable Integer levelId,
                                    @RequestBody VipLevelConfig vipLevelConfig) {
        try {
            vipLevelConfig.setLevelId(levelId);
            boolean result = vipLevelConfigService.updateById(vipLevelConfig);
            return result ? ResponseResult.success() : ResponseResult.error("更新失败");
        } catch (Exception e) {
            log.error("更新VIP等级配置失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除VIP等级配置")
    @DeleteMapping("/{levelId}")
    public ResponseResult<?> delete(@Parameter(description = "等级ID") @PathVariable Integer levelId) {
        try {
            boolean result = vipLevelConfigService.removeById(levelId);
            return result ? ResponseResult.success() : ResponseResult.error("删除失败");
        } catch (Exception e) {
            log.error("删除VIP等级配置失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}
