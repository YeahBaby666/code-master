package com.codemaster.demo.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.codemaster.demo.dto.CommandMessage;

@Controller
public class LiveExerciseController {

    private final SimpMessagingTemplate messagingTemplate;

    LiveExerciseController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // El profesor envía comandos a la sala
    @MessageMapping("/teacher/{prof}/{exercise}/command")
    public void sendCommand(@DestinationVariable String prof,
            @DestinationVariable String exercise,
            CommandMessage command) {

        // Validar si el profesor está realmente autorizado (opcional)
        // El destination sigue siendo dinámico
        String destination = "/topic/room/" + prof + "/" + exercise;

        // Reenviamos el objeto tal cual
        messagingTemplate.convertAndSend(destination, command);
    }

    
}
