package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 8. Reporte Historial (Para Docentes o Alumnos)
@Entity
@Table(name = "reportes_rendimiento")
public class ReporteRendimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private String tipoReporte; // 'semanal', 'mensual', 'cierre_curso'

    // Datos procesados listos para graficar en el frontend (estadísticas, curvas de
    // aprendizaje)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metricas_procesadas", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metricasProcesadas;

    @CreationTimestamp
    private OffsetDateTime generadoEn;
}
