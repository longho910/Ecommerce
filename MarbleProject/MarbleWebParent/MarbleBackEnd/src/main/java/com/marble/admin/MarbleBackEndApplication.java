package com.marble.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.marble.common.entity")
@EnableJpaRepositories("com.marble.admin")
public class MarbleBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarbleBackEndApplication.class, args);
    }

}
