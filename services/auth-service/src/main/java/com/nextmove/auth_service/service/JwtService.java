package com.nextmove.auth_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final long EXPIRATION = 1000 * 60 * 60;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String generateToken(String username){
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(getAlgorithm());
    }

    public String extractUsername(String token){
        return JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, String username){
        return extractUsername(token).equals(username) && !isExpired(token);
    }

    public boolean isExpired(String token){
        Date expiresAt = JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getExpiresAt();

        return expiresAt.before(new Date());
    }

}
