package com.reservasalasbackend.microservicio.usuarios.config;

import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class CoordinadorPrincipalInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${COORDINATOR_PASSWORD:}")
    private String coordinatorPassword;

    @Override
    public void run(ApplicationArguments args) {

        String correo = "coordinador@duocuc.cl";

        if (usuarioRepository.existsByCorreo(correo)) {
            System.out.println("La cuenta del coordinador ya existe.");
            return;
        }

        if (coordinatorPassword == null ||
                coordinatorPassword.length() < 8) {

            System.out.println(
                    "No se creó el coordinador: configura COORDINATOR_PASSWORD " +
                    "con al menos 8 caracteres."
            );
            return;
        }

        Usuario coordinador = new Usuario();

        coordinador.setNombre("Coordinador");
        coordinador.setCorreo(correo);
        coordinador.setRol("COORDINADOR_INFRAESTRUCTURA");
        coordinador.setPassword(
                passwordEncoder.encode(coordinatorPassword)
        );
        coordinador.setCoordinadorPrincipal(true);

        usuarioRepository.save(coordinador);

        System.out.println("Coordinador principal creado correctamente.");
    }
}
