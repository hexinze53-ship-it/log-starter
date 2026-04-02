package org.example.log.Controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.log.Repository.LogRepository;
import org.example.log.entity.Syslog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "系统日志监控管理")
@RestController/*@RestController它等同于 @Controller 加上 @ResponseBody，作用是拦截 HTTP 请求
，并将方法返回的 Java 对象自动序列化为 JSON 格式直接响应给前端，这是目前前后端分离架构的标准写法*/
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    private LogRepository logRepository;
    @ApiOperation("查询全部操作日志")
    @GetMapping("/all")
    public List<Syslog> getAllLogs(){
        return logRepository.findAll();/*直接去数据库查找所有的表*/
    }
}
