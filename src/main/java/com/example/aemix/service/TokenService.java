package com.example.aemix.service;

import com.example.aemix.config.JwtConfig;
import com.example.aemix.entity.User;
import com.example.aemix.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@AllArgsConstructor
public class TokenService {

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public String generateToken(User user) {
        var header = new JWSHeader.Builder(jwtConfig.getAlgorithm())
                .type(JOSEObjectType.JWT)
                .build();

        Instant now = Instant.now();

        var claims = new JWTClaimsSet.Builder()
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(1, java.time.temporal.ChronoUnit.HOURS)))
                .claim("role", user.getRole())
                .claim("email", user.getEmail())
                .build();

        var jwt = new SignedJWT(header, claims);

        try {
            var signer = new MACSigner(jwtConfig.getSecretKey());
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating JWT", e);
        }

        return jwt.serialize();
    }
}
