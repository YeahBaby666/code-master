package com.codemaster.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemaster.demo.model.Estudiante;
import com.codemaster.demo.model.IntentoReto;
import com.codemaster.demo.model.Reto;
import com.codemaster.demo.repository.EstudianteRepository;
import com.codemaster.demo.repository.IntentoRetoRepository;
import com.codemaster.demo.repository.RetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetoService {

    private final RetoRepository retoRepository;
    private final EstudianteRepository estudianteRepository;
    private final IntentoRetoRepository intentoRetoRepository;
    private final MotorEvaluacionIa motorEvaluacionIa;

    @Transactional
    public UUID procesarIntentoAsincrono(UUID retoId, UUID estudianteId, String codigoFuente) {
        
        Reto reto = retoRepository.findById(retoId)
                .orElseThrow(() -> new IllegalArgumentException("Reto no encontrado"));
                
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        // 1. Buscamos si ya existe un intento previo, o creamos uno nuevo
        IntentoReto intento = intentoRetoRepository
                .findFirstByRetoIdAndEstudianteIdOrderByUltimaActualizacionDesc(retoId, estudianteId)
                .orElse(new IntentoReto());

        // 2. Actualizamos el estado para bloquear la UI del frontend
        intento.setReto(reto);
        intento.setEstudiante(estudiante);
        intento.setCodigoEnviado(codigoFuente);
        intento.setEstado("evaluando_ia");
        
        // Guardamos el intento en PostgreSQL de inmediato
        intento = intentoRetoRepository.save(intento);

        // 3. Disparamos la evaluación en un hilo secundario (Fire and Forget)
        motorEvaluacionIa.evaluarCodigoEnSegundoPlano(intento.getId(), codigoFuente, reto.getConfiguracionIa());

        // 4. Retornamos el ID al controlador casi instantáneamente (0.1 segundos)
        return intento.getId();
    }
}