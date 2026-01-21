package ltd.xiaomizha.xuyou.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("monitor")
public class MonitorViewController {

    @GetMapping({"/", ""})
    public String monitorPage() {
        return "monitor";
    }

    @GetMapping("/druid")
    public String druidMonitorPage() {
        return "monitor";
    }
}