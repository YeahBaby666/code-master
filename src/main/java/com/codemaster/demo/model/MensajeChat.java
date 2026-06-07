package com.codemaster.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
// 5. Historial Chat IA (Gestión de conversación estilo Gemini)
@Entity
@Table(name = "chat_mensajes")
public class MensajeChat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    // Opcional: Si el chat ocurre dentro de un problema específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progreso_problema_id")
    private ProgresoProblema progresoProblema;

    @Column(nullable = false)
    private String rol; // 'user', 'model', 'system'

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @CreationTimestamp
    private OffsetDateTime creadoEn;
}
