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
@Tag(name = "用户资源(目录)管理", description = "用户资源(目录)管理API")
public class UserResourcesCatalogController {

    @Resource
    private UserResourcesService userResourcesService;

    @Operation(summary = "获取目录列表", description = "分页获取目录资源列表")
    @GetMapping("/catalog/list")
    public ResponseResult<?> getCatalogPage(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResources> page = new Page<>(current, pageSize);
            Page<UserResources> catalogPage = userResourcesService.page(page,
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceCategory, ResourceCategory.CATALOG)
            );
            return ResponseResultPage.ok(catalogPage.getRecords(), catalogPage.getCurrent(), catalogPage.getSize(), catalogPage.getTotal());
        } catch (Exception e) {
            log.error("获取目录列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error("获取目录列表失败");
        }
    }

    @Operation(summary = "获取目录详情", description = "根据目录ID获取目录详情")
    @GetMapping("/catalog/{resourceId}")
    public ResponseResult<?> getCatalogById(@Parameter(description = "目录ID") @PathVariable Integer resourceId) {
        try {
            UserResources catalog = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.CATALOG)
            );
            if (catalog == null) {
                return ResponseResult.error("目录不存在");
            }
            return ResponseResult.success(catalog);
        } catch (Exception e) {
            log.error("获取目录详情失败: {}", e.getMessage(), e);
            return ResponseResult.error("获取目录详情失败");
        }
    }

    @Operation(summary = "新增目录", description = "新增目录资源")
    @PostMapping("/catalog")
    public ResponseResult<?> addCatalog(@RequestBody UserResources userResources) {
        try {
            userResources.setResourceCategory(ResourceCategory.CATALOG);
            boolean result = userResourcesService.save(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增目录失败");
            }
        } catch (Exception e) {
            log.error("新增目录失败: {}", e.getMessage(), e);
            return ResponseResult.error("新增目录失败");
        }
    }

    @Operation(summary = "更新目录", description = "更新目录资源信息")
    @PutMapping("/catalog/{resourceId}")
    public ResponseResult<?> updateCatalog(@Parameter(description = "目录ID") @PathVariable Integer resourceId,
                                           @RequestBody UserResources userResources) {
        try {
            // 验证资源是否为目录
            UserResources existingCatalog = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.CATALOG)
            );
            if (existingCatalog == null) {
                return ResponseResult.error("目录不存在");
            }
            userResources.setResourceId(resourceId);
            userResources.setResourceCategory(ResourceCategory.CATALOG); // 确保类型不变
            boolean result = userResourcesService.updateById(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新目录失败");
            }
        } catch (Exception e) {
            log.error("更新目录失败: {}", e.getMessage(), e);
            return ResponseResult.error("更新目录失败");
        }
    }

    @Operation(summary = "删除目录", description = "根据目录ID删除目录")
    @DeleteMapping("/catalog/{resourceId}")
    public ResponseResult<?> deleteCatalog(@Parameter(description = "目录ID") @PathVariable Integer resourceId) {
        try {
            // 验证资源是否为目录
            UserResources existingCatalog = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.CATALOG)
            );
            if (existingCatalog == null) {
                return ResponseResult.error("目录不存在");
            }
            boolean result = userResourcesService.removeById(resourceId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除目录失败");
            }
        } catch (Exception e) {
            log.error("删除目录失败: {}", e.getMessage(), e);
            return ResponseResult.error("删除目录失败");
        }
    }

    @Operation(summary = "批量添加目录资源", description = "批量添加目录资源")
    @PostMapping("/catalog/batch")
    public ResponseResult<?> batchAddCatalogResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("目录资源列表不能为空");
            }
            // 设置所有资源为目录类型
            userResourcesList.forEach(resource -> resource.setResourceCategory(ResourceCategory.CATALOG));
            boolean result = userResourcesService.saveBatch(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加目录资源失败");
            }
        } catch (Exception e) {
            log.error("批量添加目录资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量添加目录资源失败");
        }
    }

    @Operation(summary = "批量更新目录资源", description = "批量更新目录资源")
    @PutMapping("/catalog/batch")
    public ResponseResult<?> batchUpdateCatalogResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("目录资源列表不能为空");
            }
            // 验证所有资源是否存在且为目录资源, 并设置类型
            for (UserResources resource : userResourcesList) {
                if (resource.getResourceId() == null) {
                    return ResponseResult.error("资源ID不能为空");
                }
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resource.getResourceId())
                                .eq(UserResources::getResourceCategory, ResourceCategory.CATALOG)
                );
                if (existingResource == null) {
                    return ResponseResult.error("目录资源不存在: " + resource.getResourceId());
                }
                resource.setResourceCategory(ResourceCategory.CATALOG); // 确保类型不变
            }
            boolean result = userResourcesService.updateBatchById(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新目录资源失败");
            }
        } catch (Exception e) {
            log.error("批量更新目录资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量更新目录资源失败");
        }
    }

    @Operation(summary = "批量删除目录资源", description = "批量删除目录资源")
    @DeleteMapping("/catalog/batch")
    public ResponseResult<?> batchDeleteCatalogResources(@RequestBody List<Integer> resourceIds) {
        try {
            if (resourceIds == null || resourceIds.isEmpty()) {
                return ResponseResult.error("目录资源ID列表不能为空");
            }
            // 验证所有资源是否存在且为目录资源
            for (Integer resourceId : resourceIds) {
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resourceId)
                                .eq(UserResources::getResourceCategory, ResourceCategory.CATALOG)
                );
                if (existingResource == null) {
                    return ResponseResult.error("目录资源不存在: " + resourceId);
                }
            }
            boolean result = userResourcesService.removeByIds(resourceIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除目录资源失败");
            }
        } catch (Exception e) {
            log.error("批量删除目录资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量删除目录资源失败");
        }
    }

}
