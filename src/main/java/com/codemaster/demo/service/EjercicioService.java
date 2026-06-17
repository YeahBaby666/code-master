package com.codemaster.demo.service;

import com.codemaster.demo.model.Docente;
import com.codemaster.demo.model.Ejercicio;
import com.codemaster.demo.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    public Optional<Ejercicio> buscarPorId(UUID id) {
        return ejercicioRepository.findById(id);
    }

    public Optional<Ejercicio> buscarPorCodigoAcceso(String codigoAcceso) {
        return ejercicioRepository.findByCodigoAcceso(codigoAcceso);
    }

    public List<Ejercicio> buscarPorDocente(UUID docenteId) {
        return ejercicioRepository.findByDocenteId(docenteId);
    }

    public List<Ejercicio> buscarPublicos() {
        return ejercicioRepository.findByPublicoTrue();
    }

    public Ejercicio guardar(Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }

    public void eliminar(UUID id) {
        ejercicioRepository.deleteById(id);
    }

    public String generarCodigoAccesoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ejercicioRepository.findByCodigoAcceso(codigo).isPresent());
        return codigo;
    }

    public Ejercicio crearNuevoEjercicio(String titulo, String descripcion, boolean publico, Docente docente) {
        Ejercicio ejercicio = Ejercicio.builder()
                .titulo(titulo == null ? "" : titulo.trim())
                .descripcion(descripcion == null ? "" : descripcion.trim())
                .tipoAcceso(publico ? "publico" : "privado_enlace")
                .codigoAcceso(generarCodigoAccesoUnico())
                .dificultad(1)
                .tipoEjercicio("profesor")
                .codigoInicial("# Ejercicio creado por el profesor\nprint('Hola desde el ejercicio')\n")
                .codigoActual("# Ejercicio creado por el profesor\nprint('Hola desde el ejercicio')\n")
                .habilitadoAula(false)
                .publico(publico)
                .configuracionIa(java.util.Map.of())
                .docente(docente)
                .build();
        return ejercicioRepository.save(ejercicio);
    }
}
