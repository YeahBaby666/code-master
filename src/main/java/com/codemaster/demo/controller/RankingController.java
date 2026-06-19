package com.codemaster.demo.controller;

import com.codemaster.demo.model.EjercicioEstudiante;
import com.codemaster.demo.repository.EjercicioEstudianteRepository;
import com.codemaster.demo.repository.RankingSemanalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RankingController {

    private final RankingSemanalRepository rankingRepository;
    private final EjercicioEstudianteRepository ejercicioEstudianteRepository;

    @GetMapping("/ranking")
    public String mostrarRanking(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("rankings", rankingRepository.findAllByOrderByPuntosAcumuladosDesc());
            return "ranking";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cargando el ranking: " + e.getMessage());
            return "redirect:/index";
        }
    }

    // NUEVO ENDPOINT
    @GetMapping("/ranking/historial/{nombreEstudiante}")
    public String verHistorialEstudiante(@PathVariable String nombreEstudiante, Model model, RedirectAttributes ra) {
        try {
            List<EjercicioEstudiante> historial = ejercicioEstudianteRepository.findResueltosByEstudianteNombre(nombreEstudiante);
            model.addAttribute("historial", historial);
            model.addAttribute("nombreEstudiante", nombreEstudiante);
            return "historial_ranking";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo cargar el historial.");
            return "redirect:/ranking";
        }
    }
}