package com.codemaster.demo.repository;

import com.codemaster.demo.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, UUID> {
    Optional<Ejercicio> findByCodigoAcceso(String codigoAcceso);
    List<Ejercicio> findByDocenteId(UUID docenteId);
    List<Ejercicio> findByPublicoTrue();
}
