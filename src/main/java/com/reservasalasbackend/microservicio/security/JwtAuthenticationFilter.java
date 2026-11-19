package com.reservasalasbackend.microservicio.security;

import com.reservasalasbackend.microservicio.usuarios.model.Usuario;
import com.reservasalasbackend.microservicio.usuarios.service.UsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    private static final Set<String> ROLES_VALIDOS = Set.of(
            "TRABAJADOR_RELATOR",
            "COORDINADOR_INFRAESTRUCTURA"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            if (!jwtService.validarToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String correo = jwtService.obtenerCorreo(token);

            Optional<Usuario> usuarioEncontrado =
                    usuarioService.buscarPorCorreo(correo);

            if (usuarioEncontrado.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            Usuario usuario = usuarioEncontrado.get();

            if (usuario.getRol() == null ||
                    !ROLES_VALIDOS.contains(usuario.getRol())) {

                filterChain.doFilter(request, response);
                return;
            }

            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(
                            "ROLE_" + usuario.getRol()
                    )
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            correo,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
