package com.xxx.api.apiscaffold;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.xxx.api.apiscaffold.dao")
@EnableTransactionManagement
@SpringBootApplication
public class ApiScaffoldApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiScaffoldApplication.class, args);
    }

}
