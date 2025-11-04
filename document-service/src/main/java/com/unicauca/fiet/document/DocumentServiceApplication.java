package com.unicauca.fiet.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.unicauca.fiet.document",  // paquetes de este servicio
                "com.unicauca.fiet.common"     // beans compartidos (LoggerEventPublisher/KafkaEventPublisher)
        }
)
public class DocumentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }
}
