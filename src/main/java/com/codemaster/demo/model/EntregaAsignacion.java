package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID; 
// 4. Entrega de Asignación (Donde la IA interviene para evaluar el Quiz)
@Entity
@Table(name = "entregas_asignacion")
public class EntregaAsignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private Asignacion asignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private String estado = "entregado"; // 'borrador', 'entregado', 'calificado'

    // El código final o respuestas enviadas por el alumno
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "respuestas_enviadas", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> respuestasEnviadas;

    // La evaluación autónoma de la IA (puntos a favor, errores, plagio estructural)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evaluacion_ia", columnDefinition = "jsonb")
    private Map<String, Object> evaluacionIa;

    @Column(name = "nota_final")
    private Double notaFinal; // Puede ser calculada por la IA y confirmada por el docente

    @CreationTimestamp
    private OffsetDateTime fechaEntrega;
}
