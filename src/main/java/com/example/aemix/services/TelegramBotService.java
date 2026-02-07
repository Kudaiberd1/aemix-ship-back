package com.example.aemix.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramAuthService telegramAuthService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TELEGRAM_API = "https://api.telegram.org/bot";

    /**
     * Handles /start command. If param is "login", generates login link and sends to user.
     */
    public void handleUpdate(String updateJson) {
        try {
            JsonNode root = objectMapper.readTree(updateJson);
            JsonNode message = root.path("message");
            if (message.isMissingNode()) return;

            JsonNode textNode = message.path("text");
            String text = textNode.asText(null);
            if (text == null) return;

            JsonNode from = message.path("from");
            long chatId = message.path("chat").path("id").asLong();
            long userId = from.path("id").asLong();
            String firstName = from.path("first_name").asText("");
            String lastName = from.has("last_name") ? from.path("last_name").asText() : null;
            String username = from.has("username") ? from.path("username").asText() : null;

            if (text.startsWith("/start")) {
                String param = text.length() > 6 ? text.substring(7).trim() : "";
                if ("login".equalsIgnoreCase(param)) {
                    String loginUrl = telegramAuthService.generateStartAppLoginLink(
                            userId, firstName, lastName, username
                    );
                    String msg = "Нажмите на ссылку — откроется приложение:\n\n" + loginUrl;
                    sendMessage(chatId, msg, false);
                } else {
                    sendMessage(chatId, "Привет! Отправьте /start login для входа в приложение.", false);
                }
            }
        } catch (Exception e) {
            log.error("Error handling Telegram update", e);
        }
    }

    private void sendMessage(long chatId, String text, boolean html) {
        String apiUrl = TELEGRAM_API + botToken + "/sendMessage";
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", text);
        if (html) {
            body.put("parse_mode", "HTML");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
    }

}
