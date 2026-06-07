package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 1. Estudiante (Perfil base)
@Data
@Entity
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    // Almacena un resumen de las debilidades y fortalezas del alumno
    // para que la IA lo lea antes de iniciar cualquier interacción.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "perfil_cognitivo_ia", columnDefinition = "jsonb")
    private Map<String, Object> perfilCognitivoIa; 

    @CreationTimestamp
    private OffsetDateTime fechaRegistro;
}
