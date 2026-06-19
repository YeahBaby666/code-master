package com.codemaster.demo.config;

import com.codemaster.demo.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketSecurityInterceptor implements ChannelInterceptor {
    
    private final ClassroomService classroomService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination(); 
            
            if (destination != null && destination.startsWith("/topic/room/")) {
                String providedPass = accessor.getFirstNativeHeader("pass");
                
                if (!classroomService.isAuthorized(destination, providedPass)) {
                    throw new MessageDeliveryException("Acceso denegado a la sala. Contraseña incorrecta.");
                }
            }
        }
        return message;
    }
}