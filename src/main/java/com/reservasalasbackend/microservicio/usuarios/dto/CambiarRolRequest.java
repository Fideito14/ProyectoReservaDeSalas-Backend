package com.reservasalasbackend.microservicio.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class CambiarRolRequest {

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
            regexp = "TRABAJADOR_RELATOR|COORDINADOR_INFRAESTRUCTURA",
            message = "El rol indicado no es válido"
    )
    private String rol;
}
