package com.codemaster.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.Sala;

// 2. Gestión de Salas (Prevención estricta de N+1)
@Repository
public interface SalaRepository extends JpaRepository<Sala, UUID> {
    
    // JOIN FETCH: Trae la sala y su docente en una sola consulta SQL.
    // Evita que Hibernate dispare un SELECT adicional cuando llamemos a sala.getDocente().
    @Query("SELECT s FROM Sala s JOIN FETCH s.docente WHERE s.codigoAcceso = :codigoAcceso AND s.activa = true")
    Optional<Sala> findByCodigoAccesoActiva(@Param("codigoAcceso") String codigoAcceso);
}
