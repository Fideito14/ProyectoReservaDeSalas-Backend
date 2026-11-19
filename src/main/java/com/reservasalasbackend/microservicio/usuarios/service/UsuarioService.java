package com.reservasalasbackend.microservicio.usuarios.service;

import com.reservasalasbackend.microservicio.security.JwtService;
import com.reservasalasbackend.microservicio.usuarios.dto.LoginRequest;
import com.reservasalasbackend.microservicio.usuarios.dto.RegistroRequest;
import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario registrar(RegistroRequest request) {

        if (existePorCorreo(request.getCorreo())) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        usuario.setRol("TRABAJADOR_RELATOR");

        return usuarioRepository.save(usuario);
    }

    public String iniciarSesion(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Correo o contraseña incorrectos"
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Correo o contraseña incorrectos"
            );
        }

        return jwtService.generarToken(usuario);
    }
}
