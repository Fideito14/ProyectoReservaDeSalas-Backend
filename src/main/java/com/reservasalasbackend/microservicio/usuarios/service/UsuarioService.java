
package com.reservasalasbackend.microservicio.usuarios.service;

import com.reservasalasbackend.microservicio.security.JwtService;
import com.reservasalasbackend.microservicio.usuarios.dto.LoginRequest;
import com.reservasalasbackend.microservicio.usuarios.dto.RegistroRequest;
import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        private static final Set<String> ROLES_VALIDOS = Set.of(
                        "TRABAJADOR_RELATOR",
                        "COORDINADOR_INFRAESTRUCTURA");

        // CONSULTAS DE USUARIOS

        public Optional<Usuario> buscarPorCorreo(String correo) {
                return usuarioRepository.findByCorreo(correo);
        }

        public boolean existePorCorreo(String correo) {
                return usuarioRepository.existsByCorreo(correo);
        }

        public Usuario guardar(Usuario usuario) {
                return usuarioRepository.save(usuario);
        }

        public Usuario buscarPorId(Long id) {
                return usuarioRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se encontró el usuario solicitado"));
        }

        // REGISTRO DE USUARIOS

        public Usuario registrar(RegistroRequest request) {

                if (existePorCorreo(request.getCorreo())) {
                        throw new IllegalArgumentException(
                                        "El correo ya está registrado");
                }

                Usuario usuario = new Usuario();

                usuario.setNombre(request.getNombre());
                usuario.setCorreo(request.getCorreo());
                usuario.setPassword(
                                passwordEncoder.encode(request.getPassword()));
                usuario.setRol("TRABAJADOR_RELATOR");

                return usuarioRepository.save(usuario);
        }

        // INICIO DE SESIÓN

        public String iniciarSesion(LoginRequest request) {

                Usuario usuario = usuarioRepository
                                .findByCorreo(request.getCorreo())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Correo o contraseña incorrectos"));

                if (!passwordEncoder.matches(
                                request.getPassword(),
                                usuario.getPassword())) {

                        throw new IllegalArgumentException(
                                        "Correo o contraseña incorrectos");
                }

                return jwtService.generarToken(usuario);
        }

        // ADMINISTRACIÓN DE USUARIOS

        public List<Usuario> listarUsuarios() {
                return usuarioRepository.findAll();
        }

        public Usuario cambiarRol(
                        Long id,
                        String nuevoRol,
                        String correoCoordinador) {

                // Validar que el nuevo rol exista
                if (!ROLES_VALIDOS.contains(nuevoRol)) {
                        throw new IllegalArgumentException(
                                        "El rol indicado no es válido");
                }

                // Buscar al coordinador autenticado
                Usuario coordinador = usuarioRepository
                                .findByCorreo(correoCoordinador)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se encontró al coordinador autenticado"));

                // Verificar permisos
                if (!"COORDINADOR_INFRAESTRUCTURA".equals(
                                coordinador.getRol())) {

                        throw new IllegalArgumentException(
                                        "No tienes permisos para modificar roles");
                }

                // Impedir que modifique su propio rol
                if (coordinador.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                        "No puedes modificar tu propio rol");
                }

                // Buscar al usuario que se desea modificar
                Usuario usuario = buscarPorId(id);

                // Proteger al coordinador principal
                if (usuario.isCoordinadorPrincipal()) {
                        throw new IllegalArgumentException(
                                        "No se puede modificar el rol del coordinador principal");
                }

                // Aplicar el cambio
                usuario.setRol(nuevoRol);

                return usuarioRepository.save(usuario);
        }
}
