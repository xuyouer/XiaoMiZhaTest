package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.ResourceCategory;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserResources;
import ltd.xiaomizha.xuyou.user.service.UserResourcesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("resources")
@Tag(name = "用户资源(按钮)管理", description = "用户资源(按钮)管理API")
public class UserResourcesButtonController {

    @Resource
    private UserResourcesService userResourcesService;

    @Operation(summary = "获取按钮列表", description = "分页获取按钮资源列表")
    @GetMapping("/button/list")
    public ResponseResult<?> getButtonPage(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResources> page = new Page<>(current, pageSize);
            Page<UserResources> buttonPage = userResourcesService.page(page,
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceCategory, ResourceCategory.BUTTON)
            );
            return ResponseResultPage.ok(buttonPage.getRecords(), buttonPage.getCurrent(), buttonPage.getSize(), buttonPage.getTotal());
        } catch (Exception e) {
            log.error("获取按钮列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error("获取按钮列表失败");
        }
    }

    @Operation(summary = "获取按钮详情", description = "根据按钮ID获取按钮详情")
    @GetMapping("/button/{resourceId}")
    public ResponseResult<?> getButtonById(@Parameter(description = "按钮ID") @PathVariable Integer resourceId) {
        try {
            UserResources button = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.BUTTON)
            );
            if (button == null) {
                return ResponseResult.error("按钮不存在");
            }
            return ResponseResult.success(button);
        } catch (Exception e) {
            log.error("获取按钮详情失败: {}", e.getMessage(), e);
            return ResponseResult.error("获取按钮详情失败");
        }
    }

    @Operation(summary = "新增按钮", description = "新增按钮资源")
    @PostMapping("/button")
    public ResponseResult<?> addButton(@RequestBody UserResources userResources) {
        try {
            userResources.setResourceCategory(ResourceCategory.BUTTON);
            boolean result = userResourcesService.save(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增按钮失败");
            }
        } catch (Exception e) {
            log.error("新增按钮失败: {}", e.getMessage(), e);
            return ResponseResult.error("新增按钮失败");
        }
    }

    @Operation(summary = "更新按钮", description = "更新按钮资源信息")
    @PutMapping("/button/{resourceId}")
    public ResponseResult<?> updateButton(@Parameter(description = "按钮ID") @PathVariable Integer resourceId,
                                          @RequestBody UserResources userResources) {
        try {
            // 验证资源是否为按钮
            UserResources existingButton = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.BUTTON)
            );
            if (existingButton == null) {
                return ResponseResult.error("按钮不存在");
            }
            userResources.setResourceId(resourceId);
            userResources.setResourceCategory(ResourceCategory.BUTTON); // 确保类型不变
            boolean result = userResourcesService.updateById(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新按钮失败");
            }
        } catch (Exception e) {
            log.error("更新按钮失败: {}", e.getMessage(), e);
            return ResponseResult.error("更新按钮失败");
        }
    }

    @Operation(summary = "删除按钮", description = "根据按钮ID删除按钮")
    @DeleteMapping("/button/{resourceId}")
    public ResponseResult<?> deleteButton(@Parameter(description = "按钮ID") @PathVariable Integer resourceId) {
        try {
            // 验证资源是否为按钮
            UserResources existingButton = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.BUTTON)
            );
            if (existingButton == null) {
                return ResponseResult.error("按钮不存在");
            }
            boolean result = userResourcesService.removeById(resourceId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除按钮失败");
            }
        } catch (Exception e) {
            log.error("删除按钮失败: {}", e.getMessage(), e);
            return ResponseResult.error("删除按钮失败");
        }
    }

    @Operation(summary = "批量添加按钮资源", description = "批量添加按钮资源")
    @PostMapping("/button/batch")
    public ResponseResult<?> batchAddButtonResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("按钮资源列表不能为空");
            }
            // 设置所有资源为按钮类型
            userResourcesList.forEach(resource -> resource.setResourceCategory(ResourceCategory.BUTTON));
            boolean result = userResourcesService.saveBatch(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加按钮资源失败");
            }
        } catch (Exception e) {
            log.error("批量添加按钮资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量添加按钮资源失败");
        }
    }

    @Operation(summary = "批量更新按钮资源", description = "批量更新按钮资源")
    @PutMapping("/button/batch")
    public ResponseResult<?> batchUpdateButtonResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("按钮资源列表不能为空");
            }
            // 验证所有资源是否存在且为按钮资源, 并设置类型
            for (UserResources resource : userResourcesList) {
                if (resource.getResourceId() == null) {
                    return ResponseResult.error("资源ID不能为空");
                }
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resource.getResourceId())
                                .eq(UserResources::getResourceCategory, ResourceCategory.BUTTON)
                );
                if (existingResource == null) {
                    return ResponseResult.error("按钮资源不存在: " + resource.getResourceId());
                }
                resource.setResourceCategory(ResourceCategory.BUTTON); // 确保类型不变
            }
            boolean result = userResourcesService.updateBatchById(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新按钮资源失败");
            }
        } catch (Exception e) {
            log.error("批量更新按钮资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量更新按钮资源失败");
        }
    }

    @Operation(summary = "批量删除按钮资源", description = "批量删除按钮资源")
    @DeleteMapping("/button/batch")
    public ResponseResult<?> batchDeleteButtonResources(@RequestBody List<Integer> resourceIds) {
        try {
            if (resourceIds == null || resourceIds.isEmpty()) {
                return ResponseResult.error("按钮资源ID列表不能为空");
            }
            // 验证所有资源是否存在且为按钮资源
            for (Integer resourceId : resourceIds) {
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resourceId)
                                .eq(UserResources::getResourceCategory, ResourceCategory.BUTTON)
                );
                if (existingResource == null) {
                    return ResponseResult.error("按钮资源不存在: " + resourceId);
                }
            }
            boolean result = userResourcesService.removeByIds(resourceIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除按钮资源失败");
            }
        } catch (Exception e) {
            log.error("批量删除按钮资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量删除按钮资源失败");
        }
    }

}
