package com.codemaster.demo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClassroomService {
    // Mapa: "ProfesorId/EjercicioId" -> Configuración del Aula activa
    private final Map<String, RoomSession> activeRooms = new ConcurrentHashMap<>();

    public void openRoom(String prof, String exercise, String password) {
        activeRooms.put(prof + "/" + exercise, new RoomSession(prof, exercise, password));
    }

    public boolean isAuthorized(String destination, String pass) {
        // destination viene como: /topic/room/ProfesorId/EjercicioId
        String[] parts = destination.split("/");
        if (parts.length >= 5) {
            String prof = parts[3];
            String exercise = parts[4];
            RoomSession session = activeRooms.get(prof + "/" + exercise);
            
            // Si no hay sesión activa o no tiene password, se permite. Si tiene, debe coincidir.
            return session == null || session.getPassword() == null || session.getPassword().isEmpty() || session.getPassword().equals(pass);
        }
        return true;
    }

    @Data
    @AllArgsConstructor
    public static class RoomSession {
        private String prof;
        private String exercise;
        private String password;
    }
}