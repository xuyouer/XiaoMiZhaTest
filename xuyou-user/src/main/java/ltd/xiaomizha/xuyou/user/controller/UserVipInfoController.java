package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserVipInfo;
import ltd.xiaomizha.xuyou.user.service.UserVipInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("vip-info")
@Tag(name = "用户VIP信息管理", description = "用户VIP信息管理API")
public class UserVipInfoController {

    @Resource
    private UserVipInfoService userVipInfoService;

    @Operation(summary = "获取VIP信息列表", description = "分页获取所有用户VIP信息")
    @GetMapping("/list")
    public ResponseResult<?> getVipInfoPage(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserVipInfo> vipInfoPage = userVipInfoService.getVipInfosPage(current, pageSize);
            return ResponseResultPage.ok(vipInfoPage.getRecords(), vipInfoPage.getCurrent(), vipInfoPage.getSize(), vipInfoPage.getTotal());
        } catch (Exception e) {
            log.error("获取VIP信息列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取VIP信息详情", description = "根据VIP ID获取用户VIP信息详情")
    @GetMapping("/{vipId}")
    public ResponseResult<?> getVipInfoById(@Parameter(description = "VIP ID") @PathVariable Integer vipId) {
        try {
            UserVipInfo vipInfo = userVipInfoService.getVipInfoById(vipId);
            return ResponseResult.success(vipInfo);
        } catch (Exception e) {
            log.error("获取VIP信息详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取VIP信息", description = "根据用户ID获取该用户的VIP信息")
    @GetMapping("/user/{userId}")
    public ResponseResult<?> getVipInfoByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            UserVipInfo vipInfo = userVipInfoService.getUserVipInfoByUserId(userId);
            return ResponseResult.success(vipInfo);
        } catch (Exception e) {
            log.error("根据用户ID获取VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增VIP信息", description = "新增用户VIP信息")
    @PostMapping
    public ResponseResult<?> addVipInfo(@RequestBody UserVipInfo userVipInfo) {
        try {
            boolean result = userVipInfoService.addVipInfo(userVipInfo);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增VIP信息失败");
            }
        } catch (Exception e) {
            log.error("新增VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新VIP信息", description = "更新用户VIP信息")
    @PutMapping("/{vipId}")
    public ResponseResult<?> updateVipInfo(@Parameter(description = "VIP ID") @PathVariable Integer vipId,
                                           @RequestBody UserVipInfo userVipInfo) {
        try {
            boolean result = userVipInfoService.updateVipInfo(vipId, userVipInfo);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新VIP信息失败");
            }
        } catch (Exception e) {
            log.error("更新VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除VIP信息", description = "根据VIP ID删除用户VIP信息")
    @DeleteMapping("/{vipId}")
    public ResponseResult<?> deleteVipInfo(@Parameter(description = "VIP ID") @PathVariable Integer vipId) {
        try {
            boolean result = userVipInfoService.deleteVipInfo(vipId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除VIP信息失败");
            }
        } catch (Exception e) {
            log.error("删除VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "创建默认VIP信息", description = "为用户创建默认VIP信息")
    @PostMapping("/default/{userId}")
    public ResponseResult<?> createDefaultVipInfo(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            boolean result = userVipInfoService.createDefaultUserVipInfo(userId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("创建默认VIP信息失败");
            }
        } catch (Exception e) {
            log.error("创建默认VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "激活VIP信息", description = "激活用户的VIP信息")
    @PutMapping("/activate/{userId}")
    public ResponseResult<?> activateVipInfo(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            boolean result = userVipInfoService.activateUserVipInfo(userId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("激活VIP信息失败");
            }
        } catch (Exception e) {
            log.error("激活VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加VIP信息", description = "批量添加用户VIP信息")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddVipInfo(@RequestBody List<UserVipInfo> userVipInfoList) {
        try {
            boolean result = userVipInfoService.batchAddVipInfos(userVipInfoList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加VIP信息失败");
            }
        } catch (Exception e) {
            log.error("批量添加VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新VIP信息", description = "批量更新用户VIP信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateVipInfo(@RequestBody List<UserVipInfo> userVipInfoList) {
        try {
            boolean result = userVipInfoService.batchUpdateVipInfos(userVipInfoList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新VIP信息失败");
            }
        } catch (Exception e) {
            log.error("批量更新VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除VIP信息", description = "批量删除用户VIP信息")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteVipInfo(@RequestBody List<Integer> vipIds) {
        try {
            boolean result = userVipInfoService.batchDeleteVipInfos(vipIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除VIP信息失败");
            }
        } catch (Exception e) {
            log.error("批量删除VIP信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}