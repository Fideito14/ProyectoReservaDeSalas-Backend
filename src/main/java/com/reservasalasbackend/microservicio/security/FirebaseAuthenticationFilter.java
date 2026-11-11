package com.reservasalasbackend.microservicio.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

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
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

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

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            FirebaseToken decodedToken =
                    FirebaseAuth.getInstance().verifyIdToken(token);

            String uid = decodedToken.getUid();

            Optional<Usuario> usuarioEncontrado =
                    usuarioService.buscarPorUid(uid);

            if (usuarioEncontrado.isEmpty()) {
                SecurityContextHolder.clearContext();

                responderError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "El usuario no tiene un perfil registrado"
                );
                return;
            }

            Usuario usuario = usuarioEncontrado.get();

            if (usuario.getRol() == null ||
                    !ROLES_VALIDOS.contains(usuario.getRol())) {

                SecurityContextHolder.clearContext();

                responderError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "El usuario no tiene un rol valido"
                );
                return;
            }

            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + usuario.getRol())
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            uid,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (FirebaseAuthException e) {

            SecurityContextHolder.clearContext();

            responderError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token de autenticacion invalido o expirado"
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            SecurityContextHolder.clearContext();

            responderError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar el perfil del usuario"
            );

        } catch (ExecutionException e) {

            SecurityContextHolder.clearContext();

            responderError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar el perfil del usuario"
            );
        }
    }

    private void responderError(
            HttpServletResponse response,
            int status,
            String mensaje
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"error\":\"" + mensaje + "\"}"
        );
    }
}
