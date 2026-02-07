package com.example.aemix.config;

import com.example.aemix.services.TelegramBotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotPoller {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.use-polling:true}")
    private boolean usePolling;

    private final TelegramBotService telegramBotService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TELEGRAM_API = "https://api.telegram.org/bot";
    private volatile long lastUpdateId = 0;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private ExecutorService executor;

    @PostConstruct
    public void start() {
        if (!usePolling) {
            log.info("Telegram bot: webhook mode (polling disabled)");
            return;
        }
        log.info("Telegram bot: starting long polling...");
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "telegram-bot-poller");
            t.setDaemon(false);
            return t;
        });
        executor.submit(this::pollLoop);
    }

    @PreDestroy
    public void stop() {
        running.set(false);
        if (executor != null) {
            executor.shutdown();
        }
    }

    private void pollLoop() {
        while (running.get()) {
            try {
                String url = TELEGRAM_API + botToken + "/getUpdates?offset=" + (lastUpdateId + 1) + "&timeout=30";
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    if (root.path("ok").asBoolean(false)) {
                        JsonNode updates = root.path("result");
                        for (JsonNode update : updates) {
                            long updateId = update.path("update_id").asLong();
                            lastUpdateId = updateId;
                            String updateJson = objectMapper.writeValueAsString(update);
                            telegramBotService.handleUpdate(updateJson);
                        }
                    }
                }
            } catch (Exception e) {
                if (running.get()) {
                    log.warn("Telegram poll error: {}", e.getMessage());
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
