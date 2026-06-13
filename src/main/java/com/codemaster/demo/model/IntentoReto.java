package com.codemaster.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 2. Intento de Reto (La sesión de resolución del estudiante)
@Getter
@Setter
@Entity
@Table(name = "intentos_reto")
public class IntentoReto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reto_id", nullable = false)
    private Reto reto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(length = 50, nullable = false)
    private String estado = "iniciado"; // 'iniciado', 'evaluando_ia', 'completado', 'error_compilacion'

    // El código enviado por el estudiante en su intento final
    @Column(name = "codigo_enviado", columnDefinition = "TEXT")
    private String codigoEnviado;

    // La respuesta de la IA (análisis de complejidad, buenas prácticas, errores semánticos)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "feedback_ia", columnDefinition = "jsonb")
    private Map<String, Object> feedbackIa;

    @Column(name = "puntaje_obtenido")
    private Integer puntajeObtenido;

    @UpdateTimestamp
    @Column(name = "ultima_actualizacion")
    private OffsetDateTime ultimaActualizacion;
}