package org.example.log.config;

import org.example.log.aspect.logaspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration/*告诉spring这是配置类相当于名片*/
public class LogConfig {
    @Bean
    public logaspect logaspect(){
        return new logaspect();/*相当于直接强行实例化一个java对象塞进spring容器，不在像component一样依赖于spring
        的自动扫描*/
    }
}
