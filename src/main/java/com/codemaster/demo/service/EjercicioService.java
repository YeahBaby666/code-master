package com.codemaster.demo.service;

import com.codemaster.demo.model.Docente;
import com.codemaster.demo.model.Estudiante;
import com.codemaster.demo.model.EjercicioDocente;
import com.codemaster.demo.model.EjercicioEstudiante;
import com.codemaster.demo.repository.DocenteRepository;
import com.codemaster.demo.repository.EjercicioDocenteRepository;
import com.codemaster.demo.repository.EjercicioEstudianteRepository;
import com.codemaster.demo.repository.EstudianteRepository;
import com.codemaster.demo.repository.RankingSemanalRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioDocenteRepository ejercicioDocenteRepository;
    private final EjercicioEstudianteRepository ejercicioEstudianteRepository;
    private final EstudianteRepository estudianteRepository;
    private final RankingSemanalRepository rankingSemanalRepository;
    
    // [FIX]: Inyectar el repositorio del Docente
    private final DocenteRepository docenteRepository; 

    public Optional<EjercicioDocente> buscarPorId(UUID id) {
        return ejercicioDocenteRepository.findById(id);
    }

    public List<EjercicioDocente> buscarPorDocente(String docenteNombre) {
        return ejercicioDocenteRepository.findByDocenteNombre(docenteNombre);
    }

    public List<EjercicioDocente> buscarPublicos() {
        return ejercicioDocenteRepository.findByPublicoTrue();
    }

    // Añadir junto a tus otros métodos en EjercicioService.java
    @Transactional
    public void guardarEjercicioBase(UUID idEjercicio, String docenteNombre, String jsonEstado) {
        ejercicioDocenteRepository.findByIdWithDocente(idEjercicio).ifPresent(ejercicio -> {
            // Verifica por seguridad que sea el autor original
            if (ejercicio.getDocente().getNombre().equals(docenteNombre)) {
                ejercicio.setCodigoInicial(jsonEstado);
                ejercicioDocenteRepository.save(ejercicio);
            }
        });
    }

    @Transactional
    public EjercicioDocente crearEjercicioOriginal(String docenteNombre, String titulo, String descripcion, boolean publico, Integer puntaje) {
        Docente docente = docenteRepository.findById(docenteNombre).orElseThrow();
        
        String estadoInicialJson = "[{\"id\":\"c_inicial\",\"seq\":1,\"titulo\":\"Paso 1: Inicialización\",\"codigo\":\"# Escribe tu código aquí\\n\",\"resolver\":false,\"respuesta_esperada\":\"\",\"resuelto\":false}]";
        
        EjercicioDocente ejercicio = EjercicioDocente.builder()
                .titulo(titulo == null || titulo.trim().isEmpty() ? "Sin Título" : titulo.trim())
                .descripcion(descripcion == null ? "" : descripcion.trim())
                .dificultad(1)
                .codigoInicial(estadoInicialJson)
                .publico(publico)
                .puntaje(puntaje != null ? puntaje : 0) // Guardamos el puntaje del profesor
                .configuracionIa("{}") 
                .docente(docente)
                .build();
                
        return ejercicioDocenteRepository.save(ejercicio);
    }

    public Set<UUID> obtenerIdsEjerciciosVinculados(String estudianteNombre) {
        return ejercicioEstudianteRepository.findByEstudianteNombre(estudianteNombre)
                .stream()
                .map(ee -> ee.getEjercicioOriginal().getId())
                .collect(Collectors.toSet());
    }

    @Transactional
    public EjercicioEstudiante obtenerOClonarParaEstudiante(UUID ejercicioDocenteId, String estudianteNombre) {
        EjercicioDocente original = obtenerOriginalPorId(ejercicioDocenteId);
        Estudiante estudiante = estudianteRepository.findById(estudianteNombre)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
                
        EjercicioEstudiante clon = iniciarOReanudarEjercicio(estudiante, original);
        clon.getEjercicioOriginal().getDocente().getNombre(); // Force init proxy
        return clon;
    }

    // 1. Reemplaza el buscarPorId original:
    public EjercicioDocente obtenerOriginalPorId(UUID id) {
        return ejercicioDocenteRepository.findByIdWithDocente(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio original no encontrado o corrupto"));
    }

    

   @Transactional
    public EjercicioEstudiante iniciarOReanudarEjercicio(Estudiante estudiante, EjercicioDocente ejercicioDocente) {
        Optional<EjercicioEstudiante> progresoExistente = ejercicioEstudianteRepository
                .findByEstudianteNombreAndEjercicioOriginalId(estudiante.getNombre(), ejercicioDocente.getId());

        if (progresoExistente.isPresent()) return progresoExistente.get();

        EjercicioEstudiante nuevoIntento = EjercicioEstudiante.builder()
                .ejercicioOriginal(ejercicioDocente)
                .estudiante(estudiante)
                .codigoActual(ejercicioDocente.getCodigoInicial()) 
                .resuelto(false)
                .puntajeObtenido(0) // FIX CRÍTICO: Forzamos el 0 en lugar de null
                .build();

        return ejercicioEstudianteRepository.save(nuevoIntento);
    }

    @Transactional
    public void resetearClonEstudiante(UUID ejercicioDocenteId, String estudianteNombre) {
        ejercicioEstudianteRepository.findByEstudianteNombreAndEjercicioOriginalId(estudianteNombre, ejercicioDocenteId)
                .ifPresent(ejercicio -> {
                    ejercicio.setCodigoActual(ejercicio.getEjercicioOriginal().getCodigoInicial());
                    ejercicio.setResuelto(false);
                    ejercicioEstudianteRepository.save(ejercicio);
                });
    }

    @Transactional
    public void guardarProgresoEstudiante(UUID ejercicioDocenteId, String estudianteNombre, String jsonEstado, boolean completado) {
        ejercicioEstudianteRepository.findByEstudianteNombreAndEjercicioOriginalId(estudianteNombre, ejercicioDocenteId)
                .ifPresent(ejercicio -> {
                    ejercicio.setCodigoActual(jsonEstado);

                    if (completado && !ejercicio.getResuelto()) {
                        EjercicioDocente original = ejercicio.getEjercicioOriginal();
                        Integer puntajeBase = original.getPuntaje() != null ? original.getPuntaje() : 0;
                        
                        // Siempre lo marcamos como resuelto para que la UI lo refleje y no lo vuelva a pedir
                        ejercicio.setResuelto(true);
                        ejercicio.setPuntajeObtenido(puntajeBase);

                        // DINAMISMO: Solo toca el Ranking si el profesor asignó un puntaje mayor a 0
                        if (puntajeBase > 0) {
                            rankingSemanalRepository.findByEstudianteNombre(estudianteNombre).ifPresent(ranking -> {
                                ranking.setPuntosAcumulados(ranking.getPuntosAcumulados() + puntajeBase);
                                ranking.setProblemasResueltos(ranking.getProblemasResueltos() + 1);
                                rankingSemanalRepository.save(ranking);
                            });
                        }

                    } else if (completado && ejercicio.getResuelto()) {
                        // Si el profesor editó el puntaje y el alumno vuelve a guardar, actualizamos la diferencia
                        EjercicioDocente original = ejercicio.getEjercicioOriginal();
                        Integer puntajeBase = original.getPuntaje() != null ? original.getPuntaje() : 0;
                        Integer puntajeAnterior = ejercicio.getPuntajeObtenido() != null ? ejercicio.getPuntajeObtenido() : 0;
                        int diferencia = puntajeBase - puntajeAnterior;

                        // Solo suma si la diferencia es positiva (evita restar puntos)
                        if (diferencia > 0) {
                            ejercicio.setPuntajeObtenido(puntajeBase);
                            rankingSemanalRepository.findByEstudianteNombre(estudianteNombre).ifPresent(ranking -> {
                                ranking.setPuntosAcumulados(ranking.getPuntosAcumulados() + diferencia);
                                rankingSemanalRepository.save(ranking);
                            });
                        }
                    }
                    ejercicioEstudianteRepository.save(ejercicio);
                });
    }

    // Añade esto junto a tus otros métodos en EjercicioService
    @Transactional
    public void alternarVisibilidad(UUID idEjercicio, String docenteNombre) {
        ejercicioDocenteRepository.findByIdWithDocente(idEjercicio).ifPresent(ejercicio -> {
            // Verificamos por seguridad que el ejercicio pertenezca al docente logueado
            if (ejercicio.getDocente().getNombre().equals(docenteNombre)) {
                // Invertimos el valor (Si es true pasa a false, y viceversa)
                ejercicio.setPublico(!ejercicio.getPublico());
                ejercicioDocenteRepository.save(ejercicio);
            }
        });
    }
}