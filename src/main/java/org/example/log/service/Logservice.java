package org.example.log.service;

import org.example.log.Repository.LogRepository;
import org.example.log.entity.Syslog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class Logservice {
    @Autowired
    private LogRepository logRepository;/*注入sql库管理员*/

    @Async/* 【核心魔法：只要加了这个注解，谁调用这个方法，Spring 就会秒切一个新线程去跑它！】
          底层原理也是动态代理，只有代理对象才会开启一个新线程*/
    public void saveLogAsync(Syslog syslog) {
        try {
            System.out.println("实习生 [" + Thread.currentThread().getName() + "] 正在慢吞吞地存数据库...");
            Thread.sleep(3000);//sleep是因为证明他是异步的
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logRepository.save(syslog);
        System.out.println("实习生存完啦！");
    }
}
