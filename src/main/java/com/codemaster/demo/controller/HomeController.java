package com.codemaster.demo.controller;

import com.codemaster.demo.model.EjercicioDocente;
import com.codemaster.demo.service.EjercicioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EjercicioService ejercicioService;

    private String obtenerIdSesionString(HttpSession session, String atributo) {
        Object valor = session.getAttribute(atributo);
        return valor instanceof String ? (String) valor : null;
    }

    @GetMapping({ "/", "/index" })
    public String mostrarDashboardCentral(HttpSession session, Model model) {
        String estudianteNombre = obtenerIdSesionString(session, "estudianteId");
        String docenteNombre = obtenerIdSesionString(session, "docenteId");

        if (estudianteNombre == null && docenteNombre == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("nombreEstudiante", session.getAttribute("nombreUsuario"));
        model.addAttribute("rol", session.getAttribute("rol"));

        // Cargar retos públicos
        List<EjercicioDocente> retosPublicos = ejercicioService.buscarPublicos();
        model.addAttribute("retosPublicos", retosPublicos);

        // Precargar ejercicios vinculados
        if (estudianteNombre != null) {
            Set<UUID> vinculados = ejercicioService.obtenerIdsEjerciciosVinculados(estudianteNombre);
            model.addAttribute("ejerciciosVinculados", vinculados);
        }

        return "index";
    }
}