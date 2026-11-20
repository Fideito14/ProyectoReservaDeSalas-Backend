package com.reservasalasbackend.microservicio.usuarios.controller;

import com.reservasalasbackend.microservicio.usuarios.dto.CambiarRolRequest;
import com.reservasalasbackend.microservicio.usuarios.dto.UsuarioResponse;
import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.service.UsuarioService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {

        List<UsuarioResponse> usuarios = usuarioService.listarUsuarios()
                .stream()
                .map(UsuarioResponse::desdeUsuario)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    // Consultar un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {

        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            return ResponseEntity.ok(
                    UsuarioResponse.desdeUsuario(usuario)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(404)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Cambiar el rol de un usuario
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(
            @PathVariable Long id,
            @Valid @RequestBody CambiarRolRequest request,
            Authentication authentication
    ) {

        try {
            Usuario usuario = usuarioService.cambiarRol(
                    id,
                    request.getRol(),
                    authentication.getName()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Rol actualizado correctamente",
                            "usuario", UsuarioResponse.desdeUsuario(usuario)
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}
