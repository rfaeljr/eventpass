package br.com.eventpass.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiracao-ms}")
    private long expiracaoMs;

    @Value("${jwt.expiracao-device-ms}")
    private long expiracaoDeviceMs;

    @Value("${jwt.expiracao-pareamento-ms}")
    private long expiracaoPareamentoMs;

    // ── Token de usuário (admin) ─────────────────────────────────────────────
    public String gerarTokenUsuario(Long clienteId, String email) {
        return Jwts.builder()
                .subject(clienteId.toString())
                .claim("email", email)
                .claim("tipo", "usuario")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoMs))
                .signWith(getChave())
                .compact();
    }

    // ── Token permanente de dispositivo ──────────────────────────────────────
    public String gerarTokenDispositivo(Long dispositivoId, Long clienteId) {
        return Jwts.builder()
                .subject(dispositivoId.toString())
                .claim("clienteId", clienteId)
                .claim("tipo", "dispositivo")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoDeviceMs))
                .signWith(getChave())
                .compact();
    }

    // ── Token de pareamento (uso único, 10 min) ───────────────────────────────
    public String gerarTokenPareamento(Long dispositivoId, Long clienteId) {
        return Jwts.builder()
                .subject(dispositivoId.toString())
                .claim("clienteId", clienteId)
                .claim("tipo", "pareamento")
                .claim("nonce", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoPareamentoMs))
                .signWith(getChave())
                .compact();
    }

    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extrairClienteId(String token) {
        return Long.parseLong(extrairClaims(token).getSubject());
    }

    public String extrairTipo(String token) {
        return extrairClaims(token).get("tipo", String.class);
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey getChave() {
        byte[] bytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(secret.getBytes())
        );
        return Keys.hmacShaKeyFor(bytes);
    }
}
