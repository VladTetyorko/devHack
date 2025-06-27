package com.vladte.devhack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DevHackApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevHackApplication.class, args);
    }

}
