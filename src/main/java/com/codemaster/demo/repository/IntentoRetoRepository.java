package com.codemaster.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.IntentoReto;

// 4. Intentos de Reto (El núcleo de carga de la base de datos)
@Repository
public interface IntentoRetoRepository extends JpaRepository<IntentoReto, UUID> {

    // @EntityGraph es la alternativa limpia a JOIN FETCH para métodos autogenerados.
    // Carga el estudiante y el reto inmediatamente, ignorando el FetchType.LAZY por esta única vez.
    @EntityGraph(attributePaths = {"estudiante", "reto"})
    Optional<IntentoReto> findByIdAndEstudianteId(UUID id, UUID estudianteId);

    // Búsqueda del último intento de un alumno en un reto específico
    Optional<IntentoReto> findFirstByRetoIdAndEstudianteIdOrderByUltimaActualizacionDesc(UUID retoId, UUID estudianteId);
}
