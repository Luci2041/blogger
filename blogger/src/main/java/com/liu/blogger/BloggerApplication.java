package com.liu.blogger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.liu.blogger.mapper")
public class BloggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BloggerApplication.class, args);
    }
}
