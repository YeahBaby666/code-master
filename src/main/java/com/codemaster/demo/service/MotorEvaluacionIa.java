package com.codemaster.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemaster.demo.model.IntentoReto;
import com.codemaster.demo.repository.IntentoRetoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MotorEvaluacionIa {

    private final IntentoRetoRepository intentoRetoRepository;

    // @Async le dice a Spring que ejecute esto en un ThreadPool separado
    @Async
    @Transactional // Inicia una nueva transacción en este nuevo hilo
    public CompletableFuture<Void> evaluarCodigoEnSegundoPlano(UUID intentoId, String codigoFuente, Map<String, Object> configIa) {
        
        log.info("Iniciando evaluación de IA para el intento: {}", intentoId);

        try {
            // =================================================================
            // SIMULACIÓN DE LLAMADA A LA IA (Aquí irá la integración real con REST)
            // =================================================================
            Thread.sleep(5000); // Simulamos 5 segundos de latencia de red
            
            // Simulamos la respuesta estructurada de la IA
            Map<String, Object> feedbackGenerado = new HashMap<>();
            feedbackGenerado.put("compila", true);
            feedbackGenerado.put("complejidad_ciclomatica", "O(n)");
            feedbackGenerado.put("comentarios", "Excelente uso de Streams, pero te faltó validar nulos.");
            
            int puntajeCalculado = 85; 
            // =================================================================

            // Recuperamos el intento (asegurándonos de que existe en este nuevo contexto transaccional)
            IntentoReto intento = intentoRetoRepository.findById(intentoId)
                    .orElseThrow(() -> new IllegalStateException("El intento desapareció de la BD"));

            // Actualizamos con los resultados
            intento.setFeedbackIa(feedbackGenerado);
            intento.setPuntajeObtenido(puntajeCalculado);
            intento.setEstado("completado");

            intentoRetoRepository.save(intento);
            log.info("Evaluación completada para el intento: {}", intentoId);

        } catch (Exception e) {
            log.error("Fallo crítico en la comunicación con la IA para el intento {}", intentoId, e);
            
            // Estrategia de compensación: si la IA falla, no dejamos el estado "colgado"
            intentoRetoRepository.findById(intentoId).ifPresent(intento -> {
                intento.setEstado("error_compilacion");
                intentoRetoRepository.save(intento);
            });
        }
        
        return CompletableFuture.completedFuture(null);
    }
}