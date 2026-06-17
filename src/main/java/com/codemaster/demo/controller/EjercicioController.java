package com.codemaster.demo.controller;

import com.codemaster.demo.dto.EjercicioSocketPayload;
import com.codemaster.demo.model.Docente;
import com.codemaster.demo.model.Ejercicio;
import com.codemaster.demo.repository.DocenteRepository;
import com.codemaster.demo.repository.RankingSemanalRepository;
import com.codemaster.demo.service.EjercicioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService ejercicioService;
    private final DocenteRepository docenteRepository;
    private final RankingSemanalRepository rankingRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private boolean esProfesor(HttpSession session) {
        return obtenerDocenteId(session) != null;
    }

    private UUID obtenerDocenteId(HttpSession session) {
        Object valor = session.getAttribute("docenteId");
        if (valor instanceof UUID uuid) {
            return uuid;
        }
        if (valor instanceof String texto && !texto.isBlank()) {
            try {
                return UUID.fromString(texto);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    // --------- Dashboard / ranking ---------
    @GetMapping({"/", "/index"})
    public String mostrarDashboardCentral(HttpSession session, Model model) {
        if (session.getAttribute("estudianteId") == null && session.getAttribute("docenteId") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("nombreEstudiante", session.getAttribute("nombreUsuario"));
        model.addAttribute("rol", session.getAttribute("rol"));
        return "index";
    }

    @GetMapping("/ranking")
    public String mostrarRanking(Model model) {
        model.addAttribute("rankings", rankingRepository.findAllByOrderByPuntosAcumuladosDesc());
        return "ranking";
    }

    
    

    

}
