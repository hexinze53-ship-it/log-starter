package org.example.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//这个注解的意思是只能标注在方法上不能标注在别的地方
@Retention(RetentionPolicy.RUNTIME)//面向切面编程，只有运行时间还存活才会被aop发现
public @interface Log {
    String value()default "";/*value是属性名，因为属性名是value就可以省略写
    。比如你可以直接写 @Log("新增用户")，Java 会自动把 "新增用户" 赋值给 value。
    如果你的属性名叫 description，那你就必须老老实实写 @Log(description = "新增用户")
    default意思是如果你偷懒，只写了一个 @Log（括号里什么都不填），程序也不会报错*/

}/*这整个程序是在定义一个注解*/
