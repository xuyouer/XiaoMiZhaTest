package ltd.xiaomizha.xuyou.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.common.utils.mybatis.PageUtils;
import ltd.xiaomizha.xuyou.user.entity.SystemConfigs;
import ltd.xiaomizha.xuyou.user.service.SystemConfigsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("configs")
@Tag(name = "系统配置管理", description = "系统配置管理API")
public class SystemConfigsController {

    @Resource
    private SystemConfigsService systemConfigsService;


    @GetMapping("/list")
    @Operation(summary = "获取所有配置")
    public ResponseResult<?> getList() {
        try {
            List<SystemConfigs> systemConfigs = systemConfigsService.list();
            log.info("获取系统配置成功");
            return ResponseResultPage.ok(systemConfigs);
        } catch (Exception e) {
            log.error("获取系统配置失败", e);
            return ResponseResult.error("获取系统配置失败");
        }
    }

    @GetMapping(value = {"/list/page", "/list/{current}", "/list/{current}/{pageSize}"})
    @Operation(summary = "分页获取所有配置", description = """
            优先级: 路径参数 > 查询参数 > 默认值
            
            支持多种参数传递方式:
            1. 路径参数: /list/1/10
            2. 混合参数: /list/1?size=20
            3. 查询参数: /list/page?page=2&size=15
            4. 默认参数: /list/page
            """)
    @Parameters({
            @Parameter(name = "current", description = "当前页码", example = "1"),
            @Parameter(name = "pageSize", description = "每页条数", example = "10")
    })
    public ResponseResult<?> getPageList(
            @PathVariable(required = false) Long current,
            @PathVariable(required = false) Long pageSize,
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size) {
        try {
            // 优先级: 路径参数 > 查询参数 > 默认值
            long currentPage = PageUtils.determineCurrentPage(current, page);
            long pageSizeValue = PageUtils.determinePageSize(pageSize, size);
            // 获取分页结果
            Page<SystemConfigs> pageResult = systemConfigsService.page(
                    ResponseResultPage.getPage(currentPage, pageSizeValue)
            );
            log.info("获取系统配置成功: 总记录数={}, 当前页={}, 每页大小={}",
                    pageResult.getTotal(), currentPage, pageSizeValue);
            return ResponseResultPage.ok(pageResult);
        } catch (Exception e) {
            log.error("获取系统配置失败", e);
            return ResponseResult.error("获取系统配置失败");
        }
    }

    @GetMapping("/value/{configKey}")
    @Operation(summary = "根据配置键获取配置值")
    public ResponseResult<?> getConfigValueByKey(@PathVariable String configKey) {
        try {
            String value = systemConfigsService.getConfigValueByKey(configKey);
            if (value == null) {
                return ResponseResult.error("配置不存在或已禁用");
            }
            log.info("获取配置成功: key={}, value={}", configKey, value);
            return ResponseResult.success(value);
        } catch (Exception e) {
            log.error("获取配置失败: key={}", configKey, e);
            return ResponseResult.error("获取配置失败");
        }
    }

    @GetMapping("/detail/{configKey}")
    @Operation(summary = "根据配置键获取配置对象")
    public ResponseResult<?> getConfigObjectByKey(@PathVariable String configKey) {
        try {
            log.info("{}", systemConfigsService.getConfigObjectByKey(configKey));
            SystemConfigs config = systemConfigsService.getConfigObjectByKey(configKey);
            if (config == null) {
                return ResponseResult.error("配置不存在或已禁用");
            }
            log.info("获取配置详情成功: key={}, config={}", configKey, config);
            return ResponseResult.success(config);
        } catch (Exception e) {
            log.error("获取配置详情失败: key={}", configKey, e);
            return ResponseResult.error("获取配置详情失败");
        }
    }

    @GetMapping("/check/{configKey}")
    @Operation(summary = "检查系统配置是否存在")
    public ResponseResult<?> checkConfig(@PathVariable String configKey) {
        try {
            String value = systemConfigsService.getConfigValueByKey(configKey);
            boolean exists = value != null;
            log.info("检查配置: key={}, exists={}", configKey, exists);
            return ResponseResult.success(exists);
        } catch (Exception e) {
            log.error("检查配置失败: key={}", configKey, e);
            return ResponseResult.error("检查配置失败");
        }
    }
}
