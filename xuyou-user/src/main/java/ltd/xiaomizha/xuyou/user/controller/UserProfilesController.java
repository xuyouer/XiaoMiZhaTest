package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserProfiles;
import ltd.xiaomizha.xuyou.user.service.UserProfilesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("profiles")
@Tag(name = "用户资料管理", description = "用户资料管理API")
public class UserProfilesController {

    @Resource
    private UserProfilesService userProfilesService;

    @Operation(summary = "获取用户资料列表", description = "分页获取所有用户资料")
    @GetMapping("/list")
    public ResponseResult<?> getProfilesPage(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserProfiles> profilesPage = userProfilesService.getProfilesPage(current, pageSize);
            return ResponseResultPage.ok(profilesPage.getRecords(), profilesPage.getCurrent(), profilesPage.getSize(), profilesPage.getTotal());
        } catch (Exception e) {
            log.error("获取用户资料列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取用户资料详情", description = "根据资料ID获取用户资料详情")
    @GetMapping("/{profileId}")
    public ResponseResult<?> getProfileById(@Parameter(description = "资料ID") @PathVariable Integer profileId) {
        try {
            UserProfiles profile = userProfilesService.getProfileById(profileId);
            return ResponseResult.success(profile);
        } catch (Exception e) {
            log.error("获取用户资料详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取资料", description = "根据用户ID获取该用户的资料")
    @GetMapping("/user/{userId}")
    public ResponseResult<?> getProfileByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            UserProfiles profile = userProfilesService.getUserProfileByUserId(userId);
            return ResponseResult.success(profile);
        } catch (Exception e) {
            log.error("根据用户ID获取资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增用户资料", description = "新增用户资料")
    @PostMapping
    public ResponseResult<?> addProfile(@RequestBody UserProfiles userProfiles) {
        try {
            boolean result = userProfilesService.addProfile(userProfiles);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增用户资料失败");
            }
        } catch (Exception e) {
            log.error("新增用户资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新用户资料", description = "更新用户资料信息")
    @PutMapping("/{profileId}")
    public ResponseResult<?> updateProfile(@Parameter(description = "资料ID") @PathVariable Integer profileId,
                                           @RequestBody UserProfiles userProfiles) {
        try {
            boolean result = userProfilesService.updateProfile(profileId, userProfiles);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新用户资料失败");
            }
        } catch (Exception e) {
            log.error("更新用户资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除用户资料", description = "根据资料ID删除用户资料")
    @DeleteMapping("/{profileId}")
    public ResponseResult<?> deleteProfile(@Parameter(description = "资料ID") @PathVariable Integer profileId) {
        try {
            boolean result = userProfilesService.deleteProfile(profileId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除用户资料失败");
            }
        } catch (Exception e) {
            log.error("删除用户资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加用户资料", description = "批量添加用户资料")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddProfiles(@RequestBody List<UserProfiles> userProfilesList) {
        try {
            boolean result = userProfilesService.batchAddProfiles(userProfilesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加用户资料失败");
            }
        } catch (Exception e) {
            log.error("批量添加用户资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新用户资料", description = "批量更新用户资料信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateProfiles(@RequestBody List<UserProfiles> userProfilesList) {
        try {
            boolean result = userProfilesService.batchUpdateProfiles(userProfilesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新用户资料失败");
            }
        } catch (Exception e) {
            log.error("批量更新用户资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除用户资料", description = "批量删除用户资料")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteProfiles(@RequestBody List<Integer> profileIds) {
        try {
            boolean result = userProfilesService.batchDeleteProfiles(profileIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除用户资料失败");
            }
        } catch (Exception e) {
            log.error("批量删除用户资料失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}