package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 3. Problema (Retos con enseñanza integrada)
@Entity
@Table(name = "problemas")
public class Problema {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Integer nivel; // 1 (Fácil), 2 (Medio), 3 (Difícil)

    @Column(nullable = false)
    private Integer puntosRecompensa;

    // Contiene la teoría integrada, casos de prueba y las pistas escalonadas de la
    // IA
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracion_aprendizaje", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> configuracionAprendizaje;
}