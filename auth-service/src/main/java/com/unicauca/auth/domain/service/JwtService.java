package com.unicauca.auth.domain.service;

import com.unicauca.auth.domain.model.Usuario;
import com.unicauca.auth.domain.ports.in.ValidarTokenUseCase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio de dominio para gestión de tokens JWT
 * Implementa la lógica de generación y validación de tokens
 */
@Service
public class JwtService implements ValidarTokenUseCase {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Obtiene la clave de firma para JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT para un usuario
     * @param usuario Usuario para el cual generar el token
     * @return Token JWT como String
     */
    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("nombres", usuario.getNombres());
        claims.put("apellidos", usuario.getApellidos());
        claims.put("rol", usuario.getRol().name());
        claims.put("programa", usuario.getPrograma().name());

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(usuario.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    @Override
    public String extraerRol(String token) {
        return extraerClaim(token, claims -> claims.get("rol", String.class));
    }

    /**
     * Extrae el ID del usuario desde el token
     */
    public Long extraerId(String token) {
        return extraerClaim(token, claims -> claims.get("id", Long.class));
    }

    /**
     * Extrae un claim específico del token
     */
    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extraerTodosClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Verifica si el token ha expirado
     */
    private boolean isTokenExpired(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token
     */
    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }
}