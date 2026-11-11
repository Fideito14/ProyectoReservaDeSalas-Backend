package com.reservasalasbackend.microservicio.common;

import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PruebaSeguridadController {

    private final UsuarioService usuarioService;

    @GetMapping("/prueba-seguridad")
    public Map<String, Object> probarAutenticacion(
            Authentication authentication
    ) throws InterruptedException, ExecutionException {

        Usuario usuario = usuarioService
                .buscarPorUid(authentication.getName())
                .orElseThrow();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return Map.of(
                "mensaje", "Acceso autorizado",
                "uid", usuario.getUid(),
                "nombre", usuario.getNombre(),
                "correo", usuario.getCorreo(),
                "rol", usuario.getRol(),
                "roles", roles
        );
    }
}
