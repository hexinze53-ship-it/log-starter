# 🚀 Log-Trace-Starter (轻量级异步全链路日志审计组件)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-8%2B-blue.svg)
![License](https://img.shields.io/badge/License-MIT-orange.svg)

## 📖 项目简介 (Introduction)

本项目是一个为 Spring Boot 量身定制的**无侵入式、高性能日志审计与链路追踪 Starter 组件**。
抽离并重构了开源项目中的日志监控核心思想，摒弃了传统同步写库带来的极高 I/O 损耗。通过引入**内存缓冲队列、双重触发批处理、优雅停机防丢**以及 **MDC 全链路追踪**等极致优化，在保障主业务“零延迟”的前提下，实现了对用户操作轨迹的高可靠、结构化记录。

---

## ✨ 核心架构亮点 (Core Features)

### 1. 🔌 无侵入式解耦 & 自动装配 (Plug & Play)
- **SPI 自动装配**：基于 Spring Boot `spring.factories` 机制封装为标准 Starter，业务端无需任何繁琐的 XML 或 Bean 配置，引入 POM 依赖即可使用。
- **AOP 动态切面**：通过自定义 `@Log` 注解配合 `@Around` 环绕通知，利用 Java 反射动态提取方法元数据、入参 (JSON 化) 以及执行耗时，实现监控代码与业务代码的彻底解耦。

### 2. ⚡ 高并发 I/O 削峰填谷 (High Throughput Write)
- **痛点**：传统 `@Async` 异步存库在瞬间高并发下依然会疯狂抢占数据库连接池，导致网络 I/O 拥堵。
- **破局**：引入 `LinkedBlockingQueue` 构建本地内存缓冲区。利用**后台守护线程（Daemon）** 作为消费者，采用 **“容量（满 200 条）”** 与 **“时间（超时 5 秒）”** 双重阈值触发机制，配合 Hibernate `batch_size` 将高频单次 Insert 转换为低频批量 Insert，数据库写入压力直降 90%。

### 3. 🛡️ 极端场景的数据防丢防线 (Graceful Shutdown)
- **痛点**：由于守护线程随 JVM 终止而立即销毁，若在发布重启或意外宕机瞬间，内存队列中积压的日志会彻底丢失。
- **破局**：注册 `@PreDestroy` 优雅停机钩子。在进程终止前拦截关闭信号，通过 `drainTo` 极速收缴队列残余数据。
- **并发安全设计**：在“消费端装车”与“停机端抢救”的临界区，使用极细粒度的 `synchronized` 锁。坚持**“只锁内存搬运动作，绝不锁数据库网络请求”**，在杜绝 `ConcurrentModificationException` 的同时避免了关机死锁。

### 4. 🔗 轻量级全链路追踪 (TraceId)
- 在微服务或复杂单体架构中，利用自定义 HTTP 拦截器在请求入口处生成唯一 `TraceId`。
- 结合 SLF4J 的 **MDC (底层基于 ThreadLocal 实现线程隔离)** 进行上下文透传。AOP 切面精确抓取并固化到日志实体中，实现单次 HTTP 请求轨迹的精准串联关联，大幅缩短异常排查 MTTR。

---

## 🏗️ 数据流转架构图 (Architecture)

```text
[HTTP Request] 
      │ 
      ▼
(拦截器 Interceptor) ──▶ 生成 UUID，写入 ThreadLocal (MDC)
      │
      ▼
[Controller / Service 业务代码] ──▶ 正常响应前端 (毫秒级返回)
      │
      ▼
(@Log AOP 环绕拦截) ──▶ 提取方法签名、入参、IP，从 MDC 提取 TraceId
      │
      ▼ (非阻塞 push)
[LinkedBlockingQueue 内存铁皮箱]
      │
      ▼ (后台 Daemon 线程 poll 阻塞等待)[List<Syslog> 批处理卡车] ──▶ 满足条件 (满200条 或 超时5s)
      │
      ▼ (JDBC Batch Insert)[MySQL Database]

---

##🚀 快速开始 (Quick Start)
1. 引入依赖 (通过 JitPack)
在你的 Spring Boot 项目的 pom.xml 中添加：
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.你的GitHub用户名</groupId>
        <artifactId>log-starter</artifactId>
        <version>v1.0</version>
    </dependency>
</dependencies>

---

##2. 添加数据库配置
确保你的 application.yaml 中包含以下配置（组件内置 JPA，将自动生成 sys_log 表）：
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/你的数据库名?characterEncoding=utf8
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        jdbc:
          batch_size: 200 # 开启 JPA 底层批处理

---

##3. 一键使用
在任意 Controller 或 Service 方法上添加注解
@Log("新增用户订单")
@PostMapping("/create")
public Result createOrder(...) { ... }
启动项目，访问 http://localhost:8089/doc.html 即可在 Knife4j 面板查看结构化的审计日志！
