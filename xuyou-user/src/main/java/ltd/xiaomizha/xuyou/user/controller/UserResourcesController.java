package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserResources;
import ltd.xiaomizha.xuyou.user.service.UserResourcesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("resources")
@Tag(name = "用户资源管理", description = "用户资源管理API")
public class UserResourcesController {

    @Resource
    private UserResourcesService userResourcesService;

    @Operation(summary = "获取资源列表", description = "分页获取资源列表")
    @GetMapping("/list")
    public ResponseResult<?> getResourcesPage(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResources> resourcesPage = userResourcesService.getResourcesPage(current, pageSize);
            return ResponseResultPage.ok(resourcesPage.getRecords(), resourcesPage.getCurrent(), resourcesPage.getSize(), resourcesPage.getTotal());
        } catch (Exception e) {
            log.error("获取资源列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取资源详情", description = "根据资源ID获取资源详情")
    @GetMapping("/{resourceId}")
    public ResponseResult<?> getResourceById(@Parameter(description = "资源ID") @PathVariable Integer resourceId) {
        try {
            UserResources resource = userResourcesService.getResourceById(resourceId);
            return ResponseResult.success(resource);
        } catch (Exception e) {
            log.error("获取资源详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增资源", description = "新增资源")
    @PostMapping
    public ResponseResult<?> addResource(@RequestBody UserResources userResources) {
        try {
            boolean result = userResourcesService.addResource(userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增资源失败");
            }
        } catch (Exception e) {
            log.error("新增资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新资源", description = "更新资源信息")
    @PutMapping("/{resourceId}")
    public ResponseResult<?> updateResource(@Parameter(description = "资源ID") @PathVariable Integer resourceId,
                                            @RequestBody UserResources userResources) {
        try {
            boolean result = userResourcesService.updateResource(resourceId, userResources);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新资源失败");
            }
        } catch (Exception e) {
            log.error("更新资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除资源", description = "根据资源ID删除资源")
    @DeleteMapping("/{resourceId}")
    public ResponseResult<?> deleteResource(@Parameter(description = "资源ID") @PathVariable Integer resourceId) {
        try {
            boolean result = userResourcesService.deleteResource(resourceId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除资源失败");
            }
        } catch (Exception e) {
            log.error("删除资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取资源列表", description = "根据用户ID获取该用户可访问的资源列表")
    @GetMapping("/list/user/{userId}")
    public ResponseResult<?> getResourcesByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            List<UserResources> resources = userResourcesService.getUserResourcesByUserId(userId);
            return ResponseResult.success(resources);
        } catch (Exception e) {
            log.error("根据用户ID获取资源列表失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "批量添加资源", description = "批量添加资源")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            boolean result = userResourcesService.batchAddResources(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加资源失败");
            }
        } catch (Exception e) {
            log.error("批量添加资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新资源", description = "批量更新资源")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateResources(@RequestBody List<UserResources> userResourcesList) {
        try {
            boolean result = userResourcesService.batchUpdateResources(userResourcesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新资源失败");
            }
        } catch (Exception e) {
            log.error("批量更新资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除资源", description = "批量删除资源")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteResources(@RequestBody List<Integer> resourceIds) {
        try {
            boolean result = userResourcesService.batchDeleteResources(resourceIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除资源失败");
            }
        } catch (Exception e) {
            log.error("批量删除资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}
