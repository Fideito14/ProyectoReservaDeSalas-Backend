package com.reservasalasbackend.microservicio.usuarios.controller;

import com.reservasalasbackend.microservicio.usuarios.dto.LoginRequest;
import com.reservasalasbackend.microservicio.usuarios.dto.RegistroRequest;
import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.service.UsuarioService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody RegistroRequest request
    ) {

        try {
            Usuario usuario = usuarioService.registrar(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Usuario registrado correctamente",
                            "nombre", usuario.getNombre(),
                            "correo", usuario.getCorreo(),
                            "rol", usuario.getRol()
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {

        try {
            String token = usuarioService.iniciarSesion(request);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Inicio de sesión exitoso",
                            "token", token
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }
}
