
package com.reservasalasbackend.microservicio.security;

import jakarta.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(AbstractHttpConfigurer::disable)

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                                .authorizeHttpRequests(auth -> auth

                                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                                                // Login y registro públicos
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/actuator/health")
                                                .permitAll()

                                                // Cualquier usuario autenticado puede consultar su propio perfil
                                                .requestMatchers("/api/usuarios/me").authenticated()

                                                // Administración de usuarios exclusiva del coordinador
                                                .requestMatchers("/api/usuarios/**")
                                                .hasRole("COORDINADOR_INFRAESTRUCTURA")

                                                // Las demás rutas requieren autenticación
                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
                        JwtAuthenticationFilter filter) {

                FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);

                registration.setEnabled(false);

                return registration;
        }
}
