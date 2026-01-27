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
@Tag(name = "用户资源(页面)管理", description = "用户资源(页面)管理API")
public class UserResourcesPageController {

    @Resource
    private UserResourcesService userResourcesService;

    @Operation(summary = "获取页面列表", description = "分页获取页面资源列表")
    @GetMapping("/page/list")
    public ResponseResult<?> getPageResourcePage(@RequestParam(defaultValue = "1") Integer current,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResources> page = new Page<>(current, pageSize);
            Page<UserResources> pageResourcePage = userResourcesService.page(page,
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceCategory, ResourceCategory.PAGE)
            );
            return ResponseResultPage.ok(pageResourcePage.getRecords(), pageResourcePage.getCurrent(), pageResourcePage.getSize(), pageResourcePage.getTotal());
        } catch (Exception e) {
            log.error("获取页面列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error("获取页面列表失败");
        }
    }

    @Operation(summary = "获取页面资源详情", description = "根据资源ID获取页面资源详情")
    @GetMapping("/page/{resourceId}")
    public ResponseResult<?> getPageResourceById(@Parameter(description = "资源ID") @PathVariable Integer resourceId) {
        try {
            UserResources resource = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.PAGE)
            );
            if (resource == null) {
                return ResponseResult.error("页面资源不存在");
            }
            return ResponseResult.success(resource);
        } catch (Exception e) {
            log.error("获取页面资源详情失败: {}", e.getMessage(), e);
            return ResponseResult.error("获取页面资源详情失败");
        }
    }

    @Operation(summary = "新增页面资源", description = "新增页面资源")
    @PostMapping("/page")
    public ResponseResult<?> addPageResource(@RequestBody UserResources userResources) {
        try {
            userResources.setResourceCategory(ResourceCategory.PAGE);
            boolean result = userResourcesService.save(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增页面资源失败");
            }
        } catch (Exception e) {
            log.error("新增页面资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("新增页面资源失败");
        }
    }

    @Operation(summary = "更新页面资源", description = "更新页面资源信息")
    @PutMapping("/page/{resourceId}")
    public ResponseResult<?> updatePageResource(@Parameter(description = "资源ID") @PathVariable Integer resourceId,
                                                @RequestBody UserResources userResources) {
        try {
            // 验证资源是否存在且为页面资源
            UserResources existingResource = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.PAGE)
            );
            if (existingResource == null) {
                return ResponseResult.error("页面资源不存在");
            }

            userResources.setResourceId(resourceId);
            userResources.setResourceCategory(ResourceCategory.PAGE); // 确保类型不变
            boolean result = userResourcesService.updateById(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新页面资源失败");
            }
        } catch (Exception e) {
            log.error("更新页面资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("更新页面资源失败");
        }
    }

    @Operation(summary = "删除页面资源", description = "根据资源ID删除页面资源")
    @DeleteMapping("/page/{resourceId}")
    public ResponseResult<?> deletePageResource(@Parameter(description = "资源ID") @PathVariable Integer resourceId) {
        try {
            // 验证资源是否存在且为页面资源
            UserResources existingResource = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.PAGE)
            );
            if (existingResource == null) {
                return ResponseResult.error("页面资源不存在");
            }

            boolean result = userResourcesService.removeById(resourceId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除页面资源失败");
            }
        } catch (Exception e) {
            log.error("删除页面资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("删除页面资源失败");
        }
    }

    @Operation(summary = "批量添加页面资源", description = "批量添加页面资源")
    @PostMapping("/page/batch")
    public ResponseResult<?> batchAddPageResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("页面资源列表不能为空");
            }
            // 设置所有资源为页面类型
            userResourcesList.forEach(resource -> resource.setResourceCategory(ResourceCategory.PAGE));
            boolean result = userResourcesService.saveBatch(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加页面资源失败");
            }
        } catch (Exception e) {
            log.error("批量添加页面资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量添加页面资源失败");
        }
    }

    @Operation(summary = "批量更新页面资源", description = "批量更新页面资源")
    @PutMapping("/page/batch")
    public ResponseResult<?> batchUpdatePageResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("页面资源列表不能为空");
            }
            // 验证所有资源是否存在且为页面资源, 并设置类型
            for (UserResources resource : userResourcesList) {
                if (resource.getResourceId() == null) {
                    return ResponseResult.error("资源ID不能为空");
                }
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resource.getResourceId())
                                .eq(UserResources::getResourceCategory, ResourceCategory.PAGE)
                );
                if (existingResource == null) {
                    return ResponseResult.error("页面资源不存在: " + resource.getResourceId());
                }
                resource.setResourceCategory(ResourceCategory.PAGE); // 确保类型不变
            }
            boolean result = userResourcesService.updateBatchById(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新页面资源失败");
            }
        } catch (Exception e) {
            log.error("批量更新页面资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量更新页面资源失败");
        }
    }

    @Operation(summary = "批量删除页面资源", description = "批量删除页面资源")
    @DeleteMapping("/page/batch")
    public ResponseResult<?> batchDeletePageResources(@RequestBody List<Integer> resourceIds) {
        try {
            if (resourceIds == null || resourceIds.isEmpty()) {
                return ResponseResult.error("页面资源ID列表不能为空");
            }
            // 验证所有资源是否存在且为页面资源
            for (Integer resourceId : resourceIds) {
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resourceId)
                                .eq(UserResources::getResourceCategory, ResourceCategory.PAGE)
                );
                if (existingResource == null) {
                    return ResponseResult.error("页面资源不存在: " + resourceId);
                }
            }
            boolean result = userResourcesService.removeByIds(resourceIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除页面资源失败");
            }
        } catch (Exception e) {
            log.error("批量删除页面资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量删除页面资源失败");
        }
    }
}
