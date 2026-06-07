package com.codemaster.demo.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codemaster.demo.model.Reto;
import com.codemaster.demo.repository.RetoRepository;
// Nota: Asumimos la existencia de un RetoService para la lógica asíncrona de la IA
import com.codemaster.demo.service.RetoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/retos")
@RequiredArgsConstructor
public class RetoController {

    private final RetoRepository retoRepository;
    private final RetoService retoService; // Servicio que encapsula la llamada a la IA

    // 1. Mostrar la interfaz del reto
    @GetMapping("/resolver/{codigoAcceso}")
    public String mostrarEditorReto(@PathVariable String codigoAcceso, Model model) {
        Reto reto = retoRepository.findByCodigoAcceso(codigoAcceso)
                .orElseThrow(() -> new IllegalArgumentException("Reto no encontrado o código inválido"));

        model.addAttribute("reto", reto);
        // En un escenario real, sacaríamos el ID del estudiante de la sesión de Spring Security
        model.addAttribute("estudianteId", UUID.randomUUID()); 
        
        return "reto-visor"; // Renderiza reto-visor.html
    }

    // 2. Procesar el envío del código
    @PostMapping("/enviar")
    public String enviarSolucion(
            @RequestParam("retoId") UUID retoId,
            @RequestParam("estudianteId") UUID estudianteId,
            @RequestParam("codigoFuente") String codigoFuente,
            RedirectAttributes redirectAttributes) {

        try {
            // Se delega al servicio. Este método debería tener @Async internamente
            // para no bloquear el hilo principal mientras la IA evalúa.
            UUID intentoId = retoService.procesarIntentoAsincrono(retoId, estudianteId, codigoFuente);

            // Redirigimos a una vista de "Evaluando..." o resultados usando el ID del intento
            redirectAttributes.addFlashAttribute("mensaje", "Código enviado a la IA. Evaluando...");
            return "redirect:/retos/estado/" + intentoId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al procesar tu código.");
            return "redirect:/retos"; // Redirigir a un listado general
        }
    }
    
    // 3. Pantalla de estado o resultado final
    @GetMapping("/estado/{intentoId}")
    public String verEstadoIntento(@PathVariable UUID intentoId, Model model) {
        // Aquí consultarías el IntentoRetoRepository para ver si el estado 
        // ya pasó de 'evaluando_ia' a 'completado' y mostrar el JSON de feedback.
        model.addAttribute("intentoId", intentoId);
        return "reto-estado";
    }
}