package com.codemaster.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioSocketPayload {
    private String tipo;
    private String codigoAcceso;
    private String mensaje;
    private String codigoActual;
}