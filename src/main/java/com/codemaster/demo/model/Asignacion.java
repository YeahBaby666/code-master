package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
// 3. Asignación (El "Quiz" o Tarea que el docente lanza a la sala)
@Entity
@Table(name = "asignaciones")
public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo; // 'quiz_sincrono', 'tarea_asincrona'

    @Column(name = "fecha_inicio")
    private OffsetDateTime fechaInicio;

    @Column(name = "fecha_limite")
    private OffsetDateTime fechaLimite;

    // Relación Many-to-Many con Problemas (Los retos específicos de este quiz)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "asignacion_problemas",
        joinColumns = @JoinColumn(name = "asignacion_id"),
        inverseJoinColumns = @JoinColumn(name = "problema_id")
    )
    private List<Problema> problemas;
}
