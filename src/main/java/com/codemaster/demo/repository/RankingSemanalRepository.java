package com.codemaster.demo.repository;

import com.codemaster.demo.model.RankingSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RankingSemanalRepository extends JpaRepository<RankingSemanal, UUID> {
    
    @Query("SELECT r FROM RankingSemanal r JOIN FETCH r.estudiante ORDER BY r.puntosAcumulados DESC")
    List<RankingSemanal> findAllByOrderByPuntosAcumuladosDesc();

    @Query("SELECT r FROM RankingSemanal r WHERE r.estudiante.nombre = :nombre")
    Optional<RankingSemanal> findByEstudianteNombre(@Param("nombre") String nombre);
}