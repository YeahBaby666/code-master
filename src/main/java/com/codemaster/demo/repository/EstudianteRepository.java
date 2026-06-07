package com.codemaster.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.Estudiante;

// 1. Usuarios base
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {
    Optional<Estudiante> findByCorreo(String correo);
}
