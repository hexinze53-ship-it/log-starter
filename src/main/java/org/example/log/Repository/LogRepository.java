package org.example.log.Repository;

import org.example.log.entity.Syslog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Syslog,Long> {
    // 只要继承了 JpaRepository，Spring 就会自动给你生成 save(), findById(), delete() 等方法
}
