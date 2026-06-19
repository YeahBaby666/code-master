package com.codemaster.demo.repository;

import com.codemaster.demo.model.EjercicioDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EjercicioDocenteRepository extends JpaRepository<EjercicioDocente, UUID> {
    
    @Query("SELECT e FROM EjercicioDocente e LEFT JOIN FETCH e.docente WHERE e.docente.nombre = :docenteNombre")
    List<EjercicioDocente> findByDocenteNombre(@Param("docenteNombre") String docenteNombre);
    
    @Query("SELECT e FROM EjercicioDocente e LEFT JOIN FETCH e.docente WHERE e.id = :id")
    Optional<EjercicioDocente> findByIdWithDocente(@Param("id") UUID id);
    
    @Query("SELECT e FROM EjercicioDocente e LEFT JOIN FETCH e.docente WHERE e.publico = true")
    List<EjercicioDocente> findByPublicoTrue();
}