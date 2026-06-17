package com.codemaster.demo.controller;

import com.codemaster.demo.dto.EjercicioSocketPayload;
import com.codemaster.demo.model.Docente;
import com.codemaster.demo.model.Reto;
import com.codemaster.demo.repository.DocenteRepository;
import com.codemaster.demo.repository.RankingSemanalRepository;
import com.codemaster.demo.repository.RetoRepository;
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

    private final RetoRepository retoRepository;
    private final DocenteRepository docenteRepository;
    private final RankingSemanalRepository rankingRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private String generarCodigoAcceso() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (retoRepository.findByCodigoAcceso(codigo).isPresent());
        return codigo;
    }

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

    // --------- Aula virtual ---------
    @GetMapping("/aula")
    public String aulaInicio(HttpSession session, Model model) {
        if (session.getAttribute("estudianteId") == null && session.getAttribute("docenteId") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));

        UUID docenteId = obtenerDocenteId(session);
        if (docenteId != null) {
            model.addAttribute("misEjercicios", retoRepository.findByDocenteId(docenteId));
        }
        return "aula_virtual";
    }

    @GetMapping("/aula/entrar")
    public String entrarPorCodigo(@RequestParam String codigoAcceso,
                                  @RequestParam(required = false) String password,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (session.getAttribute("estudianteId") == null && session.getAttribute("docenteId") == null) {
            return "redirect:/auth/login";
        }

        Optional<Reto> retoOpt = retoRepository.findByCodigoAcceso(codigoAcceso);
        if (retoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El código de sala no existe.");
            return "redirect:/aula";
        }

        Reto reto = retoOpt.get();
        if (!reto.getHabilitadoAula()) {
            redirectAttributes.addFlashAttribute("error", "La sala no está habilitada en este momento.");
            return "redirect:/aula";
        }

        if (reto.getSala() != null && reto.getSala().getContrasenaSala() != null
                && !reto.getSala().getContrasenaSala().isBlank()) {
            if (password == null || !reto.getSala().getContrasenaSala().equals(password)) {
                redirectAttributes.addFlashAttribute("error", "Contraseña de sala incorrecta.");
                return "redirect:/aula";
            }
        }

        model.addAttribute("reto", reto);
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("codigoAcceso", reto.getCodigoAcceso());
        return "aula_ejercicio";
    }

    @PostMapping("/aula/ejercicio/crear")
    public String crearEjercicio(@RequestParam String titulo,
                                 @RequestParam String descripcion,
                                 @RequestParam(required = false, defaultValue = "false") Boolean publico,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!esProfesor(session)) {
            redirectAttributes.addFlashAttribute("error", "Solo un profesor puede crear ejercicios.");
            return "redirect:/aula";
        }

        UUID docenteId = obtenerDocenteId(session);
        if (docenteId == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al profesor.");
            return "redirect:/aula";
        }

        Optional<Docente> docenteOpt = docenteRepository.findById(docenteId);
        if (docenteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Docente no encontrado.");
            return "redirect:/aula";
        }

        Reto reto = new Reto();
        reto.setTitulo(titulo == null ? "" : titulo.trim());
        reto.setDescripcion(descripcion == null ? "" : descripcion.trim());
        reto.setTipoAcceso(publico ? "publico" : "privado_enlace");
        reto.setCodigoAcceso(generarCodigoAcceso());
        reto.setDificultad(1);
        reto.setTipoEjercicio("profesor");
        reto.setCodigoInicial("# Ejercicio creado por el profesor\nprint('Hola desde el ejercicio')\n");
        reto.setCodigoActual(reto.getCodigoInicial());
        reto.setHabilitadoAula(false);
        reto.setPublico(publico);
        reto.setConfiguracionIa(Map.of());
        reto.setDocente(docenteOpt.get());

        try {
            retoRepository.save(reto);
            redirectAttributes.addFlashAttribute("exito", "Ejercicio creado correctamente.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar el ejercicio.");
        }

        return "redirect:/aula";
    }

    @PostMapping("/aula/ejercicio/{id}/habilitar")
    public String habilitarAula(@PathVariable UUID id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!esProfesor(session)) {
            redirectAttributes.addFlashAttribute("error", "Solo un profesor puede habilitar la sala.");
            return "redirect:/aula";
        }

        Optional<Reto> retoOpt = retoRepository.findById(id);
        if (retoOpt.isPresent()) {
            Reto reto = retoOpt.get();
            UUID docenteId = obtenerDocenteId(session);
            if (docenteId != null && reto.getDocente() != null && reto.getDocente().getId().equals(docenteId)) {
                reto.setHabilitadoAula(true);
                retoRepository.save(reto);
                messagingTemplate.convertAndSend(
                        "/topic/ejercicio/" + id,
                        new EjercicioSocketPayload(
                                "aula-habilitada",
                                reto.getCodigoAcceso(),
                                "La sala quedó abierta para escuchar el ejercicio",
                                reto.getCodigoActual()
                        )
                );
                redirectAttributes.addFlashAttribute("exito", "La sala quedó habilitada.");
                return "redirect:/aula/ejercicio/" + id;
            }
        }
        return "redirect:/aula";
    }

    @PostMapping("/aula/ejercicio/{id}/deshabilitar")
    public String deshabilitarAula(@PathVariable UUID id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!esProfesor(session)) {
            redirectAttributes.addFlashAttribute("error", "Solo un profesor puede cerrar la sala.");
            return "redirect:/aula";
        }

        Optional<Reto> retoOpt = retoRepository.findById(id);
        if (retoOpt.isPresent()) {
            Reto reto = retoOpt.get();
            UUID docenteId = obtenerDocenteId(session);
            if (docenteId != null && reto.getDocente() != null && reto.getDocente().getId().equals(docenteId)) {
                reto.setHabilitadoAula(false);
                retoRepository.save(reto);
                messagingTemplate.convertAndSend(
                        "/topic/ejercicio/" + id,
                        new EjercicioSocketPayload(
                                "aula-cerrada",
                                reto.getCodigoAcceso(),
                                "La sala quedó cerrada",
                                reto.getCodigoActual()
                        )
                );
                redirectAttributes.addFlashAttribute("exito", "La sala quedó cerrada.");
            }
        }
        return "redirect:/aula";
    }

    @PostMapping("/aula/ejercicio/{id}/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarEjercicio(@PathVariable UUID id,
                                                                @RequestParam String codigo,
                                                                HttpSession session) {
        if (!esProfesor(session) && session.getAttribute("estudianteId") == null) {
            return ResponseEntity.status(403).body(Map.of(
                    "ok", false,
                    "mensaje", "No autorizado"
            ));
        }

        Optional<Reto> retoOpt = retoRepository.findById(id);
        if (retoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reto reto = retoOpt.get();
        reto.setCodigoActual(codigo);
        retoRepository.save(reto);

        messagingTemplate.convertAndSend(
                "/topic/ejercicio/" + id,
                new EjercicioSocketPayload(
                        "ejercicio-guardado",
                        reto.getCodigoAcceso(),
                        "Ejercicio guardado",
                        codigo
                )
        );

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "mensaje", "Ejercicio guardado",
                "codigoAcceso", reto.getCodigoAcceso()
        ));
    }

    @PostMapping("/aula/ejercicio/{id}/reset")
    public String resetearEjercicio(@PathVariable UUID id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Optional<Reto> retoOpt = retoRepository.findById(id);
        if (retoOpt.isPresent()) {
            Reto reto = retoOpt.get();
            UUID docenteId = obtenerDocenteId(session);
            if ((docenteId != null && reto.getDocente() != null && reto.getDocente().getId().equals(docenteId))
                    || session.getAttribute("estudianteId") != null) {
                reto.setCodigoActual(reto.getCodigoInicial());
                retoRepository.save(reto);
                messagingTemplate.convertAndSend(
                        "/topic/ejercicio/" + id,
                        new EjercicioSocketPayload(
                                "ejercicio-reiniciado",
                                reto.getCodigoAcceso(),
                                "Ejercicio reiniciado",
                                reto.getCodigoInicial()
                        )
                );
                redirectAttributes.addFlashAttribute("exito", "Ejercicio reiniciado.");
            }
        }
        return "redirect:/aula/ejercicio/" + id;
    }

    @GetMapping("/aula/ejercicio/{id}")
    public String verEjercicio(@PathVariable UUID id,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("estudianteId") == null && session.getAttribute("docenteId") == null) {
            return "redirect:/auth/login";
        }

        Optional<Reto> retoOpt = retoRepository.findById(id);
        if (retoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ejercicio no encontrado.");
            return "redirect:/aula";
        }

        Reto reto = retoOpt.get();
        model.addAttribute("reto", reto);
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("codigoAcceso", reto.getCodigoAcceso());
        return "aula_ejercicio";
    }

    @GetMapping("/aula/ejercicio/{id}/editor")
    public String editorEjercicio(@PathVariable UUID id,
                                  HttpSession session,
                                  Model model) {
        if (session.getAttribute("estudianteId") == null && session.getAttribute("docenteId") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("reto", retoRepository.findById(id).orElse(null));
        return "codemaster_aislado";
    }

    @PostMapping("/aula/ejercicio/{id}/eliminar")
    public String eliminarEjercicio(@PathVariable UUID id,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!esProfesor(session)) {
            redirectAttributes.addFlashAttribute("error", "Solo un profesor puede eliminar ejercicios.");
            return "redirect:/aula";
        }

        Optional<Reto> retoOpt = retoRepository.findById(id);
        if (retoOpt.isPresent()) {
            Reto reto = retoOpt.get();
            UUID docenteId = obtenerDocenteId(session);
            if (docenteId != null && reto.getDocente() != null && reto.getDocente().getId().equals(docenteId)) {
                retoRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("exito", "Ejercicio eliminado.");
            }
        }

        return "redirect:/aula";
    }

    // --------- Retos públicos ---------
    @GetMapping("/retos")
    public String listarRetosPublicos(Model model) {
        model.addAttribute("retos", retoRepository.findByPublicoTrue());
        return "retos";
    }

    @GetMapping("/retos/resolver/{codigoAcceso}")
    public String mostrarEditorReto(@PathVariable String codigoAcceso, Model model) {
        Reto reto = retoRepository.findByCodigoAcceso(codigoAcceso)
                .orElseThrow(() -> new IllegalArgumentException("Reto no encontrado o código inválido"));
        model.addAttribute("reto", reto);
        model.addAttribute("codigoAcceso", reto.getCodigoAcceso());
        return "codemaster_aislado";
    }

    // --------- API / websocket fallback ---------
    @PostMapping("/api/cursos/{cursoId}/ejecutar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ejecutarCelda(
            @PathVariable String cursoId,
            @RequestBody Map<String, Object> body) {

        String bodyCursoId = body.getOrDefault("cursoId", "").toString();
        String profesorId = body.getOrDefault("profesorId", "").toString();
        String celdaId = body.getOrDefault("celdaId", "").toString();
        String codigo = body.getOrDefault("codigo", "").toString();

        if (!cursoId.equals(bodyCursoId) || !cursoId.equals("curso-demo")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "mensaje", "Curso inválido o inconsistente"
            ));
        }

        if (!"profesor-001".equals(profesorId)) {
            return ResponseEntity.status(403).body(Map.of(
                    "ok", false,
                    "mensaje", "La ejecución es ignorada porque el profesor no está autorizado"
            ));
        }

        if (celdaId.isBlank() || codigo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "mensaje", "La celda y el código son obligatorios"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "cursoId", cursoId,
                "profesorId", profesorId,
                "celdaId", celdaId,
                "mensaje", "Ejecución aceptada por la API del profesor"
        ));
    }

    @MessageMapping("/ejercicio/{id}/estado")
    public void escucharEstado(@DestinationVariable UUID id,
                               @Payload Map<String, Object> payload) {
        messagingTemplate.convertAndSend(
                "/topic/ejercicio/" + id,
                new EjercicioSocketPayload(
                        "estado-socket",
                        String.valueOf(id),
                        "Estado recibido",
                        String.valueOf(payload.getOrDefault("codigoActual", ""))
                )
        );
    }
}
