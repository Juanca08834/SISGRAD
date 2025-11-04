package com.unicauca.fiet.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.unicauca.fiet.project",  // este microservicio
                "com.unicauca.fiet.common"    // para ver LoggerEventPublisher/KafkaEventPublisher
        }
)
public class ProjectServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectServiceApplication.class, args);
    }
}
