package com.codemaster.demo.controller;

import com.codemaster.demo.model.Docente;
import com.codemaster.demo.repository.DocenteRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegistroDocenteController {

    private final DocenteRepository docenteRepository;

    @GetMapping("/registro_docente")
    public String mostrarRegistroDocente(HttpSession session) {
        if (session.getAttribute("docenteId") != null || session.getAttribute("estudianteId") != null) {
            return "redirect:/index";
        }
        return "registro_docente";
    }

    @PostMapping("/registro_docente")
    public String procesarRegistroDocente(@RequestParam String nombre,
                                          @RequestParam String correo,
                                          @RequestParam String contrasena,
                                          RedirectAttributes redirectAttributes) {

        if (docenteRepository.findByCorreo(correo).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Este correo ya está registrado como docente.");
            return "redirect:/registro_docente";
        }

        Docente docente = new Docente();
        docente.setNombre(nombre);
        docente.setCorreo(correo);
        docente.setContrasena(contrasena);
        docenteRepository.save(docente);

        redirectAttributes.addFlashAttribute("exito", "Registro de docente creado correctamente.");
        return "redirect:/auth/login";
    }
}