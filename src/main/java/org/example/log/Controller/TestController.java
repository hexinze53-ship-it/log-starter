package org.example.log.Controller;

import org.example.log.annotation.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Log("小测试呀")
    @GetMapping("/test")
    public String testAop(String name, Integer age) throws InterruptedException {
        System.out.println("...... 真正的业务逻辑正在努力执行中 ......");
        Thread.sleep(500);
        return "Hello, AOP!";
    }
}
