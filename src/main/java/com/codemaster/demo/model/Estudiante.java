package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "estudiantes")
public class Estudiante {
    
    @Id
    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "perfil_cognitivo_ia", columnDefinition = "text")
    private String perfilCognitivoIa; 

    @CreationTimestamp
    private OffsetDateTime fechaRegistro;
}