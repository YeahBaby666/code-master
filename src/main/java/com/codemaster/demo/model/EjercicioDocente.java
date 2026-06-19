package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ejercicios_docente")
public class EjercicioDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo")
    private String titulo;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(name = "publico")
    private Boolean publico = false;

    @Column(name = "tipo_acceso")
    private String tipoAcceso;

    private Integer dificultad;

    @Column(name = "tipo_ejercicio")
    private String tipoEjercicio;

    @Column(columnDefinition = "text")
    private String codigoInicial; 

    @Column(name = "configuracion_ia", columnDefinition = "text")
    private String configuracionIa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_nombre")
    private Docente docente;

    @Column(name = "puntaje", nullable = false)
    private Integer puntaje = 0; // Puntaje por defecto 0

    @CreationTimestamp
    private OffsetDateTime creadoEn;
}