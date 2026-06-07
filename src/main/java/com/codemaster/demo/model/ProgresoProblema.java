package com.codemaster.demo.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
// 4. Progreso del Problema (Historial en proceso/resuelto y cómo piensa el estudiante)
@Entity
@Table(name = "progreso_problemas")
public class ProgresoProblema {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problema_id", nullable = false)
    private Problema problema;

    @Column(nullable = false)
    private String estado = "en_proceso"; // 'en_proceso', 'resuelto', 'abandonado'

    @Column(columnDefinition = "TEXT")
    private String codigoActual;

    // La IA actualiza este JSON analizando los intentos fallidos
    // Ej: {"error_frecuente": "NullPointerException", "concepto_a_reforzar": "Optionals"}
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analisis_intentos", columnDefinition = "jsonb")
    private Map<String, Object> analisisIntentos;

    @UpdateTimestamp
    private OffsetDateTime ultimaInteraccion;
}