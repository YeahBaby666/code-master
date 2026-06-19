package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ejercicios_estudiante")
public class EjercicioEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejercicio_docente_id", nullable = false)
    private EjercicioDocente ejercicioOriginal;

    @ManyToOne(fetch = FetchType.LAZY)
    // FIX: Actualizado a estudiante_nombre
    @JoinColumn(name = "estudiante_nombre", nullable = false)
    private Estudiante estudiante;

    @Column(columnDefinition = "text")
    private String codigoActual;

    @Builder.Default // Añade esto
    @Column(nullable = false)
    private Boolean resuelto = false;

    @Builder.Default // Añade esto
    @Column(name = "puntaje_obtenido", nullable = false)
    private Integer puntajeObtenido = 0; 

    @CreationTimestamp
    private OffsetDateTime fechaInicio;

    @UpdateTimestamp
    private OffsetDateTime fechaUltimaModificacion;
}