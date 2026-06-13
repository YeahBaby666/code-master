package com.codemaster.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemaster.demo.model.Docente;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, UUID> {
    Optional<Docente> findByCorreo(String correo);
}
