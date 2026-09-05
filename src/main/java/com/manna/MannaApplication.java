package com.manna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MannaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MannaApplication.class, args);
    }

}
