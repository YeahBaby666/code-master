package com.codemaster.demo.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
// 6. Ranking Semanal
@Entity
@Table(name = "ranking_semanal")
public class RankingSemanal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer semana; // Número de semana del año (1-52)

    @Column(nullable = false)
    private Integer puntosAcumulados = 0;

    @Column(nullable = false)
    private Integer problemasResueltos = 0;
}
