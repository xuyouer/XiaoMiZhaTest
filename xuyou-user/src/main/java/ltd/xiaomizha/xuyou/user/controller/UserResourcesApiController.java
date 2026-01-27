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
@Tag(name = "用户资源(接口)管理", description = "用户资源(接口)管理API")
public class UserResourcesApiController {

    @Resource
    private UserResourcesService userResourcesService;

    @Operation(summary = "获取接口列表", description = "分页获取接口资源列表")
    @GetMapping("/api/list")
    public ResponseResult<?> getApiPage(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResources> page = new Page<>(current, pageSize);
            Page<UserResources> apiPage = userResourcesService.page(page,
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceCategory, ResourceCategory.API)
            );
            return ResponseResultPage.ok(apiPage.getRecords(), apiPage.getCurrent(), apiPage.getSize(), apiPage.getTotal());
        } catch (Exception e) {
            log.error("获取接口列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error("获取接口列表失败");
        }
    }

    @Operation(summary = "获取接口详情", description = "根据接口ID获取接口详情")
    @GetMapping("/api/{resourceId}")
    public ResponseResult<?> getApiById(@Parameter(description = "接口ID") @PathVariable Integer resourceId) {
        try {
            UserResources api = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.API)
            );
            if (api == null) {
                return ResponseResult.error("接口不存在");
            }
            return ResponseResult.success(api);
        } catch (Exception e) {
            log.error("获取接口详情失败: {}", e.getMessage(), e);
            return ResponseResult.error("获取接口详情失败");
        }
    }

    @Operation(summary = "新增接口", description = "新增接口资源")
    @PostMapping("/api")
    public ResponseResult<?> addApi(@RequestBody UserResources userResources) {
        try {
            userResources.setResourceCategory(ResourceCategory.API);
            boolean result = userResourcesService.save(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增接口失败");
            }
        } catch (Exception e) {
            log.error("新增接口失败: {}", e.getMessage(), e);
            return ResponseResult.error("新增接口失败");
        }
    }

    @Operation(summary = "更新接口", description = "更新接口资源信息")
    @PutMapping("/api/{resourceId}")
    public ResponseResult<?> updateApi(@Parameter(description = "接口ID") @PathVariable Integer resourceId,
                                       @RequestBody UserResources userResources) {
        try {
            // 验证资源是否为接口
            UserResources existingApi = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.API)
            );
            if (existingApi == null) {
                return ResponseResult.error("接口不存在");
            }
            userResources.setResourceId(resourceId);
            userResources.setResourceCategory(ResourceCategory.API); // 确保类型不变
            boolean result = userResourcesService.updateById(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新接口失败");
            }
        } catch (Exception e) {
            log.error("更新接口失败: {}", e.getMessage(), e);
            return ResponseResult.error("更新接口失败");
        }
    }

    @Operation(summary = "删除接口", description = "根据接口ID删除接口")
    @DeleteMapping("/api/{resourceId}")
    public ResponseResult<?> deleteApi(@Parameter(description = "接口ID") @PathVariable Integer resourceId) {
        try {
            // 验证资源是否为接口
            UserResources existingApi = userResourcesService.getOne(
                    new LambdaQueryWrapper<UserResources>()
                            .eq(UserResources::getResourceId, resourceId)
                            .eq(UserResources::getResourceCategory, ResourceCategory.API)
            );
            if (existingApi == null) {
                return ResponseResult.error("接口不存在");
            }
            boolean result = userResourcesService.removeById(resourceId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除接口失败");
            }
        } catch (Exception e) {
            log.error("删除接口失败: {}", e.getMessage(), e);
            return ResponseResult.error("删除接口失败");
        }
    }

    @Operation(summary = "批量添加接口资源", description = "批量添加接口资源")
    @PostMapping("/api/batch")
    public ResponseResult<?> batchAddApiResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("接口资源列表不能为空");
            }
            // 设置所有资源为接口类型
            userResourcesList.forEach(resource -> resource.setResourceCategory(ResourceCategory.API));
            boolean result = userResourcesService.saveBatch(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加接口资源失败");
            }
        } catch (Exception e) {
            log.error("批量添加接口资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量添加接口资源失败");
        }
    }

    @Operation(summary = "批量更新接口资源", description = "批量更新接口资源")
    @PutMapping("/api/batch")
    public ResponseResult<?> batchUpdateApiResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            if (userResourcesList == null || userResourcesList.isEmpty()) {
                return ResponseResult.error("接口资源列表不能为空");
            }
            // 验证所有资源是否存在且为接口资源, 并设置类型
            for (UserResources resource : userResourcesList) {
                if (resource.getResourceId() == null) {
                    return ResponseResult.error("资源ID不能为空");
                }
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resource.getResourceId())
                                .eq(UserResources::getResourceCategory, ResourceCategory.API)
                );
                if (existingResource == null) {
                    return ResponseResult.error("接口资源不存在: " + resource.getResourceId());
                }
                resource.setResourceCategory(ResourceCategory.API); // 确保类型不变
            }
            boolean result = userResourcesService.updateBatchById(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新接口资源失败");
            }
        } catch (Exception e) {
            log.error("批量更新接口资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量更新接口资源失败");
        }
    }

    @Operation(summary = "批量删除接口资源", description = "批量删除接口资源")
    @DeleteMapping("/api/batch")
    public ResponseResult<?> batchDeleteApiResources(@RequestBody List<Integer> resourceIds) {
        try {
            if (resourceIds == null || resourceIds.isEmpty()) {
                return ResponseResult.error("接口资源ID列表不能为空");
            }
            // 验证所有资源是否存在且为接口资源
            for (Integer resourceId : resourceIds) {
                UserResources existingResource = userResourcesService.getOne(
                        new LambdaQueryWrapper<UserResources>()
                                .eq(UserResources::getResourceId, resourceId)
                                .eq(UserResources::getResourceCategory, ResourceCategory.API)
                );
                if (existingResource == null) {
                    return ResponseResult.error("接口资源不存在: " + resourceId);
                }
            }
            boolean result = userResourcesService.removeByIds(resourceIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除接口资源失败");
            }
        } catch (Exception e) {
            log.error("批量删除接口资源失败: {}", e.getMessage(), e);
            return ResponseResult.error("批量删除接口资源失败");
        }
    }

}
