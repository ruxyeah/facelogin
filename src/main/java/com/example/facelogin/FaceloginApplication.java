package com.example.facelogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 人脸登录系统启动类
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.example.facelogin.config")
public class FaceloginApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceloginApplication.class, args);
    }

}
