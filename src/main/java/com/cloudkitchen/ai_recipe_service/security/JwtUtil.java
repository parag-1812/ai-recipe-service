package com.cloudkitchen.ai_recipe_service.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

/**
 * Verifies JWTs issued by the auth-service using RS256.
 * This service only VERIFIES tokens, so it holds the public key alone and
 * cannot mint tokens.
 */
@Component
public class JwtUtil {

    private final PublicKey publicKey;

    public JwtUtil(@Value("${jwt.public-key}") Resource publicKeyResource) {
        this.publicKey = PemKeyLoader.readPublicKey(publicKeyResource);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}
