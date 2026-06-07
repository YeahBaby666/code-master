package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 1. Reto de Programación (La evaluación asíncrona)
@Getter
@Setter
@Entity
@Table(name = "retos")
public class Reto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "tipo_acceso", length = 50, nullable = false)
    private String tipoAcceso = "publico"; // 'publico', 'privado_enlace', 'restringido_docente'

    @Column(name = "codigo_acceso", unique = true)
    private String codigoAcceso; // Solo se usa si es 'privado_enlace'

    @Column(nullable = false)
    private Integer dificultad; // 1, 2, 3

    // Contiene la rúbrica oculta para la IA y los casos de prueba iniciales
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracion_ia", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> configuracionIa;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn;
}