package com.codemaster.demo.controller;

import com.codemaster.demo.model.Docente;
import com.codemaster.demo.model.Estudiante;
import com.codemaster.demo.model.RankingSemanal;
import com.codemaster.demo.repository.DocenteRepository;
import com.codemaster.demo.repository.EstudianteRepository;
import com.codemaster.demo.repository.RankingSemanalRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;
    private final RankingSemanalRepository rankingSemanalRepository;


    // Método de Hashing nativo (SHA-256)
    private String hashearContrasena(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error crítico al procesar la contraseña", e);
        }
    }

    // --- VISTAS ---

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("estudianteId") != null || session.getAttribute("docenteId") != null) {
            return "redirect:/index";
        }
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(HttpSession session) {
        if (session.getAttribute("estudianteId") != null || session.getAttribute("docenteId") != null) {
            return "redirect:/index";
        }
        return "registro";
    }

    // --- LÓGICA DE PROCESAMIENTO ---

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre, 
                                   @RequestParam String correo, 
                                   @RequestParam String contrasena, 
                                   RedirectAttributes redirectAttributes) {
        
        // Verificar si el correo ya existe
        if (estudianteRepository.findByCorreo(correo).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/auth/registro";
        }

        // Crear y guardar el nuevo estudiante
        Estudiante nuevoEstudiante = new Estudiante();
        nuevoEstudiante.setNombre(nombre);
        nuevoEstudiante.setCorreo(correo);
        nuevoEstudiante.setContrasena(hashearContrasena(contrasena));

        Estudiante estudianteGuardado = estudianteRepository.save(nuevoEstudiante);

LocalDate fechaActual = LocalDate.now();
int anio = fechaActual.getYear();
int semana = fechaActual.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

RankingSemanal ranking = new RankingSemanal();
ranking.setEstudiante(estudianteGuardado);
ranking.setAnio(anio);
ranking.setSemana(semana);
ranking.setPuntosAcumulados(0);
ranking.setProblemasResueltos(0);

rankingSemanalRepository.save(ranking);


        redirectAttributes.addFlashAttribute("exito", "Registro exitoso. Inicia sesión para continuar.");
        return "redirect:/auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contrasena,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Optional<Estudiante> estudianteOpt = estudianteRepository.findByCorreo(correo);
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            if (estudiante.getContrasena().equals(hashearContrasena(contrasena))) {
                session.setAttribute("estudianteId", estudiante.getId());
                session.setAttribute("estudianteNombre", estudiante.getNombre());
                session.setAttribute("nombreUsuario", estudiante.getNombre());
                session.setAttribute("rol", "estudiante");
                return "redirect:/index";
            }
        }

        Optional<Docente> docenteOpt = docenteRepository.findByCorreo(correo);
        if (docenteOpt.isPresent()) {
            Docente docente = docenteOpt.get();
            if (docente.getContrasena().equals(contrasena)) {
                session.setAttribute("docenteId", docente.getId());
                session.setAttribute("docenteNombre", docente.getNombre());
                session.setAttribute("nombreUsuario", docente.getNombre());
                session.setAttribute("rol", "profesor");
                return "redirect:/index";
            }
        }

        redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas.");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Destruye la sesión en el servidor
        redirectAttributes.addFlashAttribute("exito", "Has cerrado sesión correctamente.");
        return "redirect:/auth/login";
    }
}