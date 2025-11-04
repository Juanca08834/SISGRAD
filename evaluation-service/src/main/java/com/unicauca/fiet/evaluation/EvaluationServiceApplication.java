package com.unicauca.fiet.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.unicauca.fiet.evaluation",  // paquetes propios del servicio
                "com.unicauca.fiet.common"       // beans compartidos: EventPublisher, eventos, etc.
        }
)
public class EvaluationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvaluationServiceApplication.class, args);
    }
}
