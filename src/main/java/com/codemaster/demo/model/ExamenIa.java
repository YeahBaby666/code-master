package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 7. Examen con IA (Evaluación escrita)
@Entity
@Table(name = "examenes_ia")
public class ExamenIa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private String estado = "asignado"; // 'asignado', 'en_curso', 'calificado'

    // El examen generado por la IA (preguntas abiertas, requerimientos)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preguntas_generadas", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> preguntasGeneradas;

    // Las respuestas escritas por el alumno en texto libre o código
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "respuestas_estudiante", columnDefinition = "jsonb")
    private Map<String, Object> respuestasEstudiante;

    @Column(columnDefinition = "TEXT")
    private String feedbackEvaluacionIa;

    @Column
    private Double notaFinal;
}
