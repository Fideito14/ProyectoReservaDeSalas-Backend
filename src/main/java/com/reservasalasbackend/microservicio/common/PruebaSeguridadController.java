package com.reservasalasbackend.microservicio.common;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PruebaSeguridadController {

    @GetMapping("/prueba-seguridad")
    public Map<String, Object> probarAutenticacion(
            Authentication authentication
    ) {

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return Map.of(
                "mensaje", "Acceso autorizado",
                "usuario", authentication.getName(),
                "roles", roles
        );
    }
}