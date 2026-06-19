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

        // SANITIZACIÓN ESTRICTA: Minúsculas y eliminación total de espacios en blanco
        String nombreSaneado = nombre.toLowerCase().replaceAll("\\s+", "");
        String correoSaneado = correo.toLowerCase().replaceAll("\\s+", "");

        // Validación doble: Verifica colisión en el Correo y en el Nombre (Primary Key)
        if (docenteRepository.findByCorreo(correoSaneado).isPresent() || docenteRepository.findById(nombreSaneado).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Este nombre de docente o correo ya está registrado.");
            return "redirect:/registro_docente";
        }

        Docente docente = new Docente();
        // Se inyecta el dato ya saneado
        docente.setNombre(nombreSaneado);
        docente.setCorreo(correoSaneado);
        docente.setContrasena(contrasena);
        docenteRepository.save(docente);

        redirectAttributes.addFlashAttribute("exito", "Registro de docente creado correctamente.");
        return "redirect:/auth/login";
    }
}