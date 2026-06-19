package com.codemaster.demo.controller;

import com.codemaster.demo.model.EjercicioDocente;
import com.codemaster.demo.service.ClassroomService;
import com.codemaster.demo.service.EjercicioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService ejercicioService;
    private final ClassroomService classroomService;

    private String obtenerIdSesionString(HttpSession session, String atributo) {
        Object valor = session.getAttribute(atributo);
        return valor instanceof String ? (String) valor : null;
    }

    @GetMapping("/panel_docente")
    public String panelDocente(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            String docenteNombre = obtenerIdSesionString(session, "docenteId");
            if (docenteNombre == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión inválida o no eres docente.");
                return "redirect:/index";
            }

            List<EjercicioDocente> misEjercicios = ejercicioService.buscarPorDocente(docenteNombre);
            model.addAttribute("misEjercicios", misEjercicios);
            return "panel_docente";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error cargando panel docente: " + e.getMessage());
            return "redirect:/index";
        }
    }

    @PostMapping("/ejercicio/crear")
    public String crearEjercicioDocente(
            HttpSession session,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam(defaultValue = "false") boolean isPublico,
            @RequestParam(defaultValue = "0") Integer puntaje, // Nuevo
            RedirectAttributes ra) {

        String docenteNombre = obtenerIdSesionString(session, "docenteId");
        if (docenteNombre == null)
            return "redirect:/index";

        try {
            ejercicioService.crearEjercicioOriginal(docenteNombre, titulo, descripcion, isPublico, puntaje);

            ra.addFlashAttribute("mensaje", "Ejercicio estructurado y guardado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Falla de integridad al generar ejercicio: " + e.getMessage());
        }

        return "redirect:/panel_docente";
    }

    @GetMapping("/ejercicio/{idEjercicio}")
    public String entrarEjercicio(@PathVariable UUID idEjercicio, HttpSession session, Model model, RedirectAttributes ra) {
        String estudianteNombre = obtenerIdSesionString(session, "estudianteId");
        String docenteNombre = obtenerIdSesionString(session, "docenteId");

        // FIX: Respetar las variables isLive y livePass si vienen del redirect (hostLiveSession o joinLiveSession)
        if (!model.containsAttribute("isLive")) {
            model.addAttribute("isLive", false);
            model.addAttribute("livePass", "");
        }

        try {
            if (estudianteNombre != null) {
                var clon = ejercicioService.obtenerOClonarParaEstudiante(idEjercicio, estudianteNombre);
                var original = clon.getEjercicioOriginal();
                
                model.addAttribute("tituloReto", original.getTitulo());
                model.addAttribute("rol", "estudiante");
                model.addAttribute("celdasJson", clon.getCodigoActual()); 
                model.addAttribute("profesorId", original.getDocente().getNombre());
                model.addAttribute("ejercicioId", original.getId().toString());
                
                return "codemaster_aislado";
                
            } else if (docenteNombre != null) {
                var original = ejercicioService.obtenerOriginalPorId(idEjercicio);
                
                model.addAttribute("tituloReto", original.getTitulo());
                model.addAttribute("rutaSocket", original.getDocente().getNombre() + "/" + original.getId().toString());
                model.addAttribute("rol", "profesor");
                model.addAttribute("celdasJson", original.getCodigoInicial()); 
                model.addAttribute("profesorId", original.getDocente().getNombre());
                model.addAttribute("ejercicioId", original.getId().toString());
                
                return "codemaster_aislado";
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Fallo interno. El ejercicio pudo haber sido eliminado. " + e.getMessage());
            return "redirect:/index";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/ejercicio/{idEjercicio}/host")
    public String hostLiveSession(@PathVariable UUID idEjercicio, @RequestParam String password, HttpSession session,
            RedirectAttributes ra) {
        String docenteNombre = obtenerIdSesionString(session, "docenteId");
        if (docenteNombre == null)
            return "redirect:/index";

        var ejercicio = ejercicioService.buscarPorId(idEjercicio).orElseThrow();

        classroomService.openRoom(docenteNombre, ejercicio.getId().toString(), password);

        ra.addFlashAttribute("isLive", true);
        ra.addFlashAttribute("livePass", password);
        return "redirect:/ejercicio/" + ejercicio.getId();
    }

    @GetMapping("/ejercicio/live/{profNombre}/{ejerId}")
    public String joinLiveSession(@PathVariable String profNombre, @PathVariable UUID ejerId,
            @RequestParam(required = false) String pass, HttpSession session, RedirectAttributes ra) {
        String estudianteNombre = obtenerIdSesionString(session, "estudianteId");
        if (estudianteNombre == null)
            return "redirect:/auth/login";

        String profNombreSaneado = profNombre.toLowerCase().trim();

        var ejercicio = ejercicioService.buscarPorId(ejerId).orElse(null);
        if (ejercicio == null || !ejercicio.getDocente().getNombre().equals(profNombreSaneado)) {
            ra.addFlashAttribute("error", "Ruta de sala inválida o no encontrada.");
            return "redirect:/index";
        }

        String destination = "/topic/room/" + profNombreSaneado + "/" + ejerId;
        if (!classroomService.isAuthorized(destination, pass)) {
            ra.addFlashAttribute("error", "Contraseña incorrecta o el aula está cerrada.");
            return "redirect:/index";
        }

        ra.addFlashAttribute("isLive", true);
        ra.addFlashAttribute("livePass", pass);
        return "redirect:/ejercicio/" + ejercicio.getId();
    }

    @PostMapping("/ejercicio/{idDocente}/reset")
    public String resetearEjercicio(@PathVariable UUID idDocente, HttpSession session, RedirectAttributes ra) {
        String estudianteNombre = obtenerIdSesionString(session, "estudianteId");

        if (estudianteNombre != null) {
            ejercicioService.resetearClonEstudiante(idDocente, estudianteNombre);
            ra.addFlashAttribute("mensaje", "El ejercicio ha sido reseteado a su estado original.");
            return "redirect:/ejercicio/" + idDocente;
        }

        return "redirect:/index";
    }

    @PostMapping("/ejercicio/{idEjercicio}/guardar")
    @ResponseBody
    public ResponseEntity<String> guardarProgresoOffline(
            @PathVariable UUID idEjercicio,
            @RequestParam(defaultValue = "false") boolean completado, // Nuevo
            @RequestBody String estadoJson,
            HttpSession session) {

        String estudianteNombre = obtenerIdSesionString(session, "estudianteId");
        String docenteNombre = obtenerIdSesionString(session, "docenteId");

        if (estudianteNombre != null) {
            // Le pasamos el flag al service
            ejercicioService.guardarProgresoEstudiante(idEjercicio, estudianteNombre, estadoJson, completado);
            return ResponseEntity.ok("Progreso de estudiante guardado");
        } else if (docenteNombre != null) {
            ejercicioService.guardarEjercicioBase(idEjercicio, docenteNombre, estadoJson);
            return ResponseEntity.ok("Plantilla base actualizada");
        }
        return ResponseEntity.status(403).body("No autorizado");
    }


    // Añade este endpoint dentro de EjercicioController
    @PostMapping("/ejercicio/{idEjercicio}/toggle-public")
    public String alternarVisibilidadEjercicio(@PathVariable UUID idEjercicio, HttpSession session, RedirectAttributes ra) {
        String docenteNombre = obtenerIdSesionString(session, "docenteId");
        
        if (docenteNombre != null) {
            ejercicioService.alternarVisibilidad(idEjercicio, docenteNombre);
            ra.addFlashAttribute("mensaje", "Estado de visibilidad actualizado exitosamente.");
        } else {
            ra.addFlashAttribute("error", "No autorizado.");
        }
        
        return "redirect:/panel_docente";
    }
}