package ltd.xiaomizha.xuyou.common.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.utils.geo.IpRegionUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("ip")
@RequiredArgsConstructor
public class IpController {

    @Resource
    private IpRegionUtil ipRegionUtil;

    /**
     * 手动传入IP查询归属地
     *
     * @param ip 目标IP
     * @return 归属地结果
     */
    @GetMapping("/query")
    public String queryIp(@RequestParam String ip) {
        return ipRegionUtil.getIpRegion(ip);
    }

    /**
     * 获取当前请求的真实IP并查询归属地
     *
     * @param request HTTP请求对象
     * @return IP归属地信息
     */
    @GetMapping("/current")
    public String getCurrentIpRegion(HttpServletRequest request) {
        return ipRegionUtil.getRequestIpRegion(request);
    }

}
