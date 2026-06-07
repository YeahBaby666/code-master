package com.codemaster.demo.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.MensajeChat;

// 5. Historial de Chat IA (Control estricto de memoria RAM)
@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, UUID> {

    // LIMITACIÓN DE MEMORIA: NUNCA extraer todo el chat de golpe en listas (List<MensajeChat>).
    // Obligamos al uso de Pageable para procesar historiales largos en bloques (ej. de 20 en 20).
    @Query("SELECT m FROM MensajeChat m WHERE m.estudiante.id = :estudianteId ORDER BY m.creadoEn ASC")
    Page<MensajeChat> findHistorialPaginadoPorEstudiante(@Param("estudianteId") UUID estudianteId, Pageable pageable);
}
