package com.codemaster.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.Reto;

// 3. Retos y Evaluaciones Asíncronas
@Repository
public interface RetoRepository extends JpaRepository<Reto, UUID> {
    
    Optional<Reto> findByCodigoAcceso(String codigoAcceso);

    List<Reto> findByDocenteId(UUID docenteId);

    List<Reto> findBySalaId(UUID salaId);

    List<Reto> findByPublicoTrue();
    
    Page<Reto> findByTipoAccesoOrderByDificultadAsc(String tipoAcceso, Pageable pageable);
}
