package com.codemaster.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // Mapeamos tanto la raíz como /index a este panel
    @GetMapping({"/", "/index"})
    public String mostrarDashboardCentral(HttpSession session, Model model) {
        
        // Validación manual de sesión (Control de Acceso)
        if (session.getAttribute("estudianteId") == null) {
            return "redirect:/auth/login";
        }

        // Pasamos el nombre del estudiante a la vista para personalizar el saludo
        model.addAttribute("nombreEstudiante", session.getAttribute("estudianteNombre"));
        
        return "index"; // Renderiza templates/index.html
    }
}