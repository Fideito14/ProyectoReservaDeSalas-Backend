package com.reservasalasbackend.microservicio;

import com.reservasalasbackend.microservicio.security.JwtSecretInitializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroservicioApplication {

    public static void main(String[] args) {

        SpringApplication application =
                new SpringApplication(MicroservicioApplication.class);

        application.addInitializers(new JwtSecretInitializer());

        application.run(args);
    }
}
