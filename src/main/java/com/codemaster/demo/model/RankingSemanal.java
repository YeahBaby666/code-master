package com.codemaster.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "ranking_semanal")
public class RankingSemanal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    // FIX: Actualizado a estudiante_nombre
    @JoinColumn(name = "estudiante_nombre", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer semana;

    @Column(nullable = false)
    private Integer puntosAcumulados = 0;

    @Column(nullable = false)
    private Integer problemasResueltos = 0;

    @Column(nullable = false)
    private LocalDate fechaInicioSemana;

    @Column(nullable = false)
    private LocalDate fechaFinSemana;
}