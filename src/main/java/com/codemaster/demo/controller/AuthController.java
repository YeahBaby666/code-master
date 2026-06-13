package com.codemaster.demo.controller;

import com.codemaster.demo.model.Estudiante;
import com.codemaster.demo.repository.EstudianteRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EstudianteRepository estudianteRepository;

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
        // Si ya hay una sesión activa, redirigir al dashboard/retos
        if (session.getAttribute("estudianteId") != null) {
            return "redirect:/index"; 
        }
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(HttpSession session) {
        if (session.getAttribute("estudianteId") != null) {
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

        estudianteRepository.save(nuevoEstudiante);

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
            String hashIngresado = hashearContrasena(contrasena);

            if (estudiante.getContrasena().equals(hashIngresado)) {
                // Iniciar sesión guardando el ID y el Nombre en la HttpSession de Tomcat
                session.setAttribute("estudianteId", estudiante.getId());
                session.setAttribute("estudianteNombre", estudiante.getNombre());
                
                return "redirect:/index"; // Redirigir al panel principal
            }
        }

        // Falla genérica para no dar pistas a atacantes
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