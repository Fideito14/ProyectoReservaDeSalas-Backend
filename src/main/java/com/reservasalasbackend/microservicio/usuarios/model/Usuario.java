package com.reservasalasbackend.microservicio.usuarios.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private String uid;
    private String correo;
    private String nombre;
    private String rol;
}
