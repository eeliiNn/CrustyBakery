package com.crustybakery.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CrustybakeryWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrustybakeryWebApplication.class, args);
    }
}
