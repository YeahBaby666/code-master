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
    private String tipoAcceso = "publico";

    @Column(name = "codigo_acceso", unique = true)
    private String codigoAcceso;

    @Column(nullable = false)
    private Integer dificultad = 1;

    @Column(name = "tipo_ejercicio", nullable = false)
    private String tipoEjercicio = "libre"; // libre, profesor, ia

    @Column(name = "codigo_inicial", columnDefinition = "TEXT")
    private String codigoInicial = "";

    @Column(name = "codigo_actual", columnDefinition = "TEXT")
    private String codigoActual = "";

    @Column(name = "habilitado_aula", nullable = false)
    private Boolean habilitadoAula = false;

    @Column(name = "publico", nullable = false)
    private Boolean publico = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    private Sala sala;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracion_ia", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> configuracionIa = Map.of();

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn;
}