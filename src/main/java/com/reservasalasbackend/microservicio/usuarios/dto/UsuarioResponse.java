package com.reservasalasbackend.microservicio.usuarios.dto;

import com.reservasalasbackend.microservicio.usuarios.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String rol,
        boolean coordinadorPrincipal
) {

    public static UsuarioResponse desdeUsuario(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.isCoordinadorPrincipal()
        );
    }
}
