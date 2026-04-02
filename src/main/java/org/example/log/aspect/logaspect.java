package org.example.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.log.Repository.LogRepository;
import org.example.log.annotation.Log;
import org.example.log.entity.Syslog;
import org.example.log.service.Logservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect/*告诉spring，这不是普通类这是一个拥有拦截功能的管家*/
public class logaspect {
    @Autowired
    private Logservice logservice;
    @Pointcut("@annotation(org.example.log.annotation.Log)")/*
    这个注解的意思为只要谁带上了这个注解你就要盯着他*/
    public void logPointcut() {
        //标记而已
    }


    /*我的理解这是个拦截器在拦截前拦截后做的事*/
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("========== 【AOP管家】发现目标，开始计时！ ==========");
        long begintime = System.currentTimeMillis();
        Object result = joinPoint.proceed();/*对方法放行，不然卡着了*/
        long time = System.currentTimeMillis() - begintime;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();/*把所有的方法装进一个麻袋，getSignature在获得一个方法的签名
        ，强转成 (MethodSignature)？因为管家抓到的可能是一个属性，也可能是一个类，但我们很确定我们抓到的是一个“方法”*/
        Method method = signature.getMethod();//通过身份证拿到这个方法
        Log logAnnotation = method.getAnnotation(Log.class);/*看看他有没有log对象有就塞进去没有就null*/
        String operation = "";/*类似@Log(value = "删除用户信息")，operation是删除用户信息*/
        if (logAnnotation != null) {
            operation = logAnnotation.value();
        }
        Object[] args = joinPoint.getArgs();/*从麻袋里掏出所有的参数*/
        String params="";
        try {
            ObjectMapper mapper = new ObjectMapper();/*新建一个spring自带的json转换器*/
            params = mapper.writeValueAsString(args);
        } catch (Exception e) {
            params="参数转换失败";/*防止有特殊参数无法转成json*/
        }
        String className = joinPoint.getTarget().getClass().getName();/*拿到类名*/
        String methodName = signature.getName();/*获得方法名*/
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();/*
        spring收到请求会把她的请求打包，我们拿到打包盒*/
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteAddr();
        Syslog syslog = new Syslog();
        syslog.setLogType("INFO");
        syslog.setDescription(operation);
        syslog.setRequestIp(ip);
        syslog.setMethod(className+","+methodName+"()");
        syslog.setParams(params);
        syslog.setTime(time);
        logservice.saveLogAsync(syslog);
        System.out.println("====== 【AOP管家】情报已移交后台实习生，管家立刻去送外卖了！ ======");



       /* System.out.println("====== 【AOP管家】收集到以下情报 ======");
        System.out.println("操作描述: " + operation);
        System.out.println("请求类名: " + className);
        System.out.println("请求方法: " + methodName + "()");
        System.out.println("请求 IP : " + ip);
        System.out.println("执行耗时: " + time + " 毫秒");
        System.out.println("=======================================");
*/
        return result;
    }
}/*这是我们的aop切面管家，这挺底层的至少平常的业务代码用不到他*/
