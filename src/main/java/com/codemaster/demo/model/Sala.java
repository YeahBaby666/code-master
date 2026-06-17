package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

// 2. Sala (Equivalente a un "Curso" o "Sección" dictada por un docente)
@Getter
@Setter
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
    private String nombre;

    @Column(unique = true, nullable = false, length = 20)
    private String codigoAcceso;

    @Column(nullable = false)
    private String contrasenaSala = "";

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
