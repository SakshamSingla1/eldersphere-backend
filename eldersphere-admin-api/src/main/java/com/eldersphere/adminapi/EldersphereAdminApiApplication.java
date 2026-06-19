package com.eldersphere.adminapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.eldersphere.adminapi", "com.eldersphere.core"})
@EnableJpaRepositories(basePackages = "com.eldersphere.core.repository")
@EntityScan(basePackages = "com.eldersphere.core.entities")
public class EldersphereAdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EldersphereAdminApiApplication.class, args);
    }
}
