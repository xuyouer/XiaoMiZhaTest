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
@Tag(name = "用户资源(菜单)管理", description = "用户资源(菜单)管理API")
public class UserResourcesMenuController {

    @Resource
    private UserResourcesService userResourcesService;

    @Operation(summary = "获取菜单列表", description = "分页获取菜单资源列表")
    @GetMapping("/menu/list")
    public ResponseResult<?> getMenuPage(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResources> page = new Page<>(current, pageSize);
            Page<UserResources> menuPage = userResourcesService.page(page,
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceCategory, ResourceCategory.MENU)
            );
            return ResponseResultPage.ok(menuPage.getRecords(), menuPage.getCurrent(), menuPage.getSize(), menuPage.getTotal());
        } catch (Exception e) {
            log.error("获取菜单列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error("获取菜单列表失败");
        }
    }

    @Operation(summary = "获取菜单详情", description = "根据菜单ID获取菜单详情")
    @GetMapping("/menu/{resourceId}")
    public ResponseResult<?> getMenuById(@Parameter(description = "菜单ID") @PathVariable Integer resourceId) {
        try {
            UserResources menu = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.MENU)
            );
            if (menu == null) {
                return ResponseResult.error("菜单不存在");
            }
            return ResponseResult.success(menu);
        } catch (Exception e) {
            log.error("获取菜单详情失败: {}", e.getMessage(), e);
            return ResponseResult.error("获取菜单详情失败");
        }
    }

    @Operation(summary = "新增菜单", description = "新增菜单资源")
    @PostMapping("/menu")
    public ResponseResult<?> addMenu(@RequestBody UserResources userResources) {
        try {
            userResources.setResourceCategory(ResourceCategory.MENU);
            boolean result = userResourcesService.save(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增菜单失败");
            }
        } catch (Exception e) {
            log.error("新增菜单失败: {}", e.getMessage(), e);
            return ResponseResult.error("新增菜单失败");
        }
    }

    @Operation(summary = "更新菜单", description = "更新菜单资源信息")
    @PutMapping("/menu/{resourceId}")
    public ResponseResult<?> updateMenu(@Parameter(description = "菜单ID") @PathVariable Integer resourceId,
                                        @RequestBody UserResources userResources) {
        try {
            // 验证资源是否为菜单
            UserResources existingMenu = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.MENU)
            );
            if (existingMenu == null) {
                return ResponseResult.error("菜单不存在");
            }
            userResources.setResourceId(resourceId);
            userResources.setResourceCategory(ResourceCategory.MENU); // 确保类型不变
            boolean result = userResourcesService.updateById(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新菜单失败");
            }
        } catch (Exception e) {
            log.error("更新菜单失败: {}", e.getMessage(), e);
            return ResponseResult.error("更新菜单失败");
        }
    }

    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单")
    @DeleteMapping("/menu/{resourceId}")
    public ResponseResult<?> deleteMenu(@Parameter(description = "菜单ID") @PathVariable Integer resourceId) {
        try {
            // 验证资源是否为菜单
            UserResources existingMenu = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.MENU)
            );
            if (existingMenu == null) {
                return ResponseResult.error("菜单不存在");
            }
            boolean result = userResourcesService.removeById(resourceId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除菜单失败");
            }
        } catch (Exception e) {
            log.error("删除菜单失败: {}", e.getMessage(), e);
            return ResponseResult.error("删除菜单失败");
        }
    }

    @Operation(summary = "批量添加菜单资源", description = "批量添加菜单资源")
    @PostMapping("/menu/batch")
    public ResponseResult<?> batchAddMenuResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("菜单资源列表不能为空");
            }
            // 设置所有资源为菜单类型
            userResourcesList.forEach(resource -> resource.setResourceCategory(ResourceCategory.MENU));
            boolean result = userResourcesService.saveBatch(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加菜单资源失败");
            }
        } catch (Exception e) {
            log.error("批量添加菜单资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量添加菜单资源失败");
        }
    }

    @Operation(summary = "批量更新菜单资源", description = "批量更新菜单资源")
    @PutMapping("/menu/batch")
    public ResponseResult<?> batchUpdateMenuResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("菜单资源列表不能为空");
            }
            // 验证所有资源是否存在且为菜单资源, 并设置类型
            for (UserResources resource : userResourcesList) {
                if (resource.getResourceId() == null) {
                    return ResponseResult.error("资源ID不能为空");
                }
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resource.getResourceId())
                                .eq(UserResources::getResourceCategory, ResourceCategory.MENU)
                );
                if (existingResource == null) {
                    return ResponseResult.error("菜单资源不存在: " + resource.getResourceId());
                }
                resource.setResourceCategory(ResourceCategory.MENU); // 确保类型不变
            }
            boolean result = userResourcesService.updateBatchById(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新菜单资源失败");
            }
        } catch (Exception e) {
            log.error("批量更新菜单资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量更新菜单资源失败");
        }
    }

    @Operation(summary = "批量删除菜单资源", description = "批量删除菜单资源")
    @DeleteMapping("/menu/batch")
    public ResponseResult<?> batchDeleteMenuResources(@RequestBody List<Integer> resourceIds) {
        try {
            if (resourceIds == null || resourceIds.isEmpty()) {
                return ResponseResult.error("菜单资源ID列表不能为空");
            }
            // 验证所有资源是否存在且为菜单资源
            for (Integer resourceId : resourceIds) {
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resourceId)
                                .eq(UserResources::getResourceCategory, ResourceCategory.MENU)
                );
                if (existingResource == null) {
                    return ResponseResult.error("菜单资源不存在: " + resourceId);
                }
            }
            boolean result = userResourcesService.removeByIds(resourceIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除菜单资源失败");
            }
        } catch (Exception e) {
            log.error("批量删除菜单资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量删除菜单资源失败");
        }
    }

}
