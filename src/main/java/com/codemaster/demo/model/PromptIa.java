package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

// 2. Instrucciones IA (Prompts y configuraciones del sistema)
@Entity
@Table(name = "prompts_ia")
public class PromptIa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String identificador; // Ej: "EXAMEN_JAVA_BASICO", "ASISTENTE_ERRORES"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String instruccionSistema; // El prompt maestro

    @Column(nullable = false)
    private Double temperaturaDefault = 0.3;
}
