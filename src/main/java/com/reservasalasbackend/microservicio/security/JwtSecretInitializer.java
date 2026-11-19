package com.reservasalasbackend.microservicio.security;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

public class JwtSecretInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {

        try {
            String secret = System.getenv("JWT_SECRET");

            if (secret == null || secret.isBlank()) {
                secret = System.getProperty("JWT_SECRET");
            }

            if (secret == null || secret.isBlank()) {

                Path secretFile = Path.of(".jwt-secret");

                if (Files.exists(secretFile)) {
                    secret = Files.readString(secretFile).trim();
                } else {
                    byte[] key = new byte[32];
                    new SecureRandom().nextBytes(key);

                    secret = Base64.getEncoder()
                            .encodeToString(key);

                    Files.writeString(secretFile, secret);
                }
            }

            byte[] decoded = Base64.getDecoder().decode(secret);

            if (decoded.length < 32) {
                throw new IllegalStateException(
                        "El secreto JWT debe tener al menos 32 bytes"
                );
            }

            context.getEnvironment().getPropertySources().addFirst(
                    new MapPropertySource(
                            "jwtSecret",
                            Map.of("jwt.secret", secret)
                    )
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo inicializar el secreto JWT", e
            );
        }
    }
}
