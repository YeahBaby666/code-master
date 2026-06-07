package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
// 3. Pista Desbloqueada (Registro de ayudas solicitadas durante el reto)
@Entity
@Table(name = "pistas_desbloqueadas")
public class PistaDesbloqueada {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intento_reto_id", nullable = false)
    private IntentoReto intentoReto;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String solicitudEstudiante; // Ej: "No entiendo por qué me da NullPointerException"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String respuestaIa; // La guía generada por la IA

    // Penalización en el puntaje final por usar la IA como muleta
    @Column(name = "penalizacion_puntos", nullable = false)
    private Integer penalizacionPuntos = 0;

    @CreationTimestamp
    private OffsetDateTime solicitadaEn;
}
