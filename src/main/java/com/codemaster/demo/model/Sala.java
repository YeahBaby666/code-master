package com.codemaster.demo.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
// 2. Sala (Equivalente a un "Curso" o "Sección" dictada por un docente)
@Entity
@Table(name = "salas")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(nullable = false)
    private String nombre; // Ej: "Programación Orientada a Objetos - Sección A"

    @Column(unique = true, nullable = false, length = 10)
    private String codigoAcceso; // Código corto (Ej: "X7B9-PW") para que los alumnos se unan

    @Column(nullable = false)
    private Boolean activa = true;

    // Relación Many-to-Many con Estudiantes
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sala_estudiantes",
        joinColumns = @JoinColumn(name = "sala_id"),
        inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> estudiantes;
}
