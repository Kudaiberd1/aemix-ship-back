package com.example.aemix.repositories;

import com.example.aemix.entities.TelegramLoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface TelegramLoginTokenRepository extends JpaRepository<TelegramLoginToken, Long> {

    Optional<TelegramLoginToken> findByTokenAndExpiresAtAfter(String token, Instant expiresAt);

    void deleteByExpiresAtBefore(Instant instant);
}
