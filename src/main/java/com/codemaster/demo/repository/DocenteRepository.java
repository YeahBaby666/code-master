package com.codemaster.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.codemaster.demo.model.Docente;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, String> {
    Optional<Docente> findByCorreo(String correo);
}