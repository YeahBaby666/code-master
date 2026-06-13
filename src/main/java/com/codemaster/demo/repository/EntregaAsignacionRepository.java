package com.codemaster.demo.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.EntregaAsignacion;

// 6. Entregas y Asignaciones
@Repository
public interface EntregaAsignacionRepository extends JpaRepository<EntregaAsignacion, UUID> {
    
    // Extrae todas las entregas de una asignación (quiz) puntual para el dashboard del profesor,
    // extrayendo la información del estudiante simultáneamente para evitar consultas N+1 en bucle.
    @EntityGraph(attributePaths = {"estudiante"})
    List<EntregaAsignacion> findByAsignacionId(UUID asignacionId);
}
