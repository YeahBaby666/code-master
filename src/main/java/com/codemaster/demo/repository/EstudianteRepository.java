package com.codemaster.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.codemaster.demo.model.Estudiante;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {
    Optional<Estudiante> findByCorreo(String correo);
}