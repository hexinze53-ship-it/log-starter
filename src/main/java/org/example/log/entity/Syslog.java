package org.example.log.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity/*实体类对应数据库的表*/
@Table(name = "sys_log")
public class Syslog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logType; // 日志类型 (INFO/ERROR)
    private String description; // 操作描述 (干了什么)
    private String requestIp; // 请求IP
    private String method; // 请求方法 (类名+方法名)
    private String params; // 请求参数
    private Long time; // 耗时(毫秒)
    private String username; // 操作人
    private String exceptionDetail; // 报错信息

    @CreationTimestamp // 保存时自动填入当前时间
    private Timestamp createTime;
}
