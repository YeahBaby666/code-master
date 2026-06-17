package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false, unique = true)
    private String codigoAcceso;

    @Column(nullable = false)
    private Boolean habilitadoAula = false;

    @Column(nullable = false)
    private Boolean publico = false;

    @Column(nullable = false)
    private String tipoAcceso;

    private Integer dificultad;

    @Column(nullable = false)
    private String tipoEjercicio;

    @Column(columnDefinition = "text")
    private String codigoInicial;

    @Column(columnDefinition = "text")
    private String codigoActual;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracion_ia", columnDefinition = "jsonb")
    private Map<String, Object> configuracionIa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @CreationTimestamp
    private OffsetDateTime creadoEn;
}
