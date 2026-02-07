package com.example.aemix.controllers;

import com.example.aemix.services.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/telegram")
public class TelegramBotController {

    private final TelegramBotService telegramBotService;

    /**
     * Webhook for Telegram Bot API.
     * Set in Telegram: https://api.telegram.org/bot<TOKEN>/setWebhook?url=<YOUR_SERVER>/api/telegram/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String update) {
        telegramBotService.handleUpdate(update);
        return ResponseEntity.ok().build();
    }
}
