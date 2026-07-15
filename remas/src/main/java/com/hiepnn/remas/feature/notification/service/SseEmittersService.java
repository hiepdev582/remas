package com.hiepnn.remas.feature.notification.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseEmittersService {

    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L); // 24 hours timeout
        
        userEmitters.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>()).add(emitter);
        
        emitter.onCompletion(() -> {
            log.info("Emitter completed for user: {}", username);
            removeEmitter(username, emitter);
        });
        
        emitter.onTimeout(() -> {
            log.info("Emitter timed out for user: {}", username);
            emitter.complete();
            removeEmitter(username, emitter);
        });
        
        emitter.onError((e) -> {
            log.error("Emitter error for user: " + username, e);
            emitter.complete();
            removeEmitter(username, emitter);
        });

        // Send an initial handshake event
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("Connection established successfully"));
        } catch (IOException e) {
            emitter.complete();
            removeEmitter(username, emitter);
        }

        return emitter;
    }

    private void removeEmitter(String username, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(username);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(username);
            }
        }
    }

    public void sendToUser(String username, String eventName, Object data) {
        if (username == null) return;
        List<SseEmitter> emitters = userEmitters.get(username);
        if (emitters == null || emitters.isEmpty()) return;

        List<SseEmitter> failedEmitters = new ArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            } catch (IOException | IllegalStateException e) {
                log.warn("Failed to send event to emitter for user {}, cleaning up", username, e);
                failedEmitters.add(emitter);
            }
        }
        
        if (!failedEmitters.isEmpty()) {
            emitters.removeAll(failedEmitters);
            if (emitters.isEmpty()) {
                userEmitters.remove(username);
            }
            for (SseEmitter emitter : failedEmitters) {
                try {
                    emitter.complete();
                } catch (Exception ignored) {}
            }
        }
    }

    public void sendToAll(String eventName, Object data) {
        for (String username : userEmitters.keySet()) {
            sendToUser(username, eventName, data);
        }
    }
}
