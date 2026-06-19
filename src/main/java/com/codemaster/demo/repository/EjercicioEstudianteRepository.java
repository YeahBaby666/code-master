package com.codemaster.demo.repository;

import com.codemaster.demo.model.EjercicioEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EjercicioEstudianteRepository extends JpaRepository<EjercicioEstudiante, UUID> {
    Optional<EjercicioEstudiante> findByEstudianteNombreAndEjercicioOriginalId(String estudianteNombre, UUID ejercicioOriginalId);
    
    List<EjercicioEstudiante> findByEstudianteNombre(String estudianteNombre);

    // Nuevo: Traer el historial de resueltos
    @Query("SELECT e FROM EjercicioEstudiante e JOIN FETCH e.ejercicioOriginal o JOIN FETCH o.docente WHERE e.estudiante.nombre = :nombre AND e.resuelto = true ORDER BY e.fechaUltimaModificacion DESC")
    List<EjercicioEstudiante> findResueltosByEstudianteNombre(@Param("nombre") String nombre);
}