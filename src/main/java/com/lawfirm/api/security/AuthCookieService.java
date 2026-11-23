package com.lawfirm.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Servicio responsable de la gestión de cookies de autenticación (BFF / Reverse Proxy).
 *
 * Principio de Responsabilidad Única (SRP):
 * Centraliza la construcción y el ciclo de vida de la cookie HttpOnly de autenticación,
 * aislando esta lógica de presentación HTTP de los controladores y filtros.
 */
@Service
public class AuthCookieService {

    private final String cookieName;
    private final long expirationMs;
    private final boolean secure;

    public AuthCookieService(
            @Value("${app.security.jwt.cookie-name:lawfirm_token}") String cookieName,
            @Value("${app.security.jwt.expiration-ms:3600000}") long expirationMs,
            @Value("${app.security.jwt.cookie-secure:false}") boolean secure
    ) {
        this.cookieName = cookieName;
        this.expirationMs = expirationMs;
        this.secure = secure;
    }

    /**
     * Construye la cookie "lawfirm_token" para el inicio de sesión.
     *
     * IMPORTANTE PARA LA ARQUITECTURA BFF / NEXT.JS REWRITES:
     * - NO se define el atributo 'domain' (debe ser null/omitido). De esta manera, el navegador
     *   la trata como una cookie "Host-Only", asociándola automáticamente al dominio del frontend
     *   en Vercel (o localhost en desarrollo) cuando la respuesta pasa a través del proxy inverso de Next.js.
     * - httpOnly(true): Previene ataques XSS impidiendo acceso via JavaScript del cliente.
     * - sameSite("Lax"): Protección balanceada contra ataques CSRF, compatible con navegaciones seguras.
     * - secure: Dinámico/Condicional. 'false' en desarrollo local (HTTP) y 'true' en producción (HTTPS).
     * - path("/"): Disponible para toda la aplicación frontend.
     */
    public ResponseCookie createAuthCookie(String token) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .sameSite("Lax")
                // Sin .domain(...) -> Host-Only Cookie
                .build();
    }

    /**
     * Construye la cookie para invalidar la sesión en logout.
     *
     * Establece maxAge(0) y mantiene idénticos atributos (path, httpOnly, secure, sameSite)
     * para asegurar que todos los navegadores modernos (Safari ITP, Chrome, Firefox)
     * coincidan con la cookie existente y la eliminen inmediatamente.
     */
    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                // Sin .domain(...) -> Host-Only Cookie
                .build();
    }

    public String getCookieName() {
        return cookieName;
    }

    public boolean isSecure() {
        return secure;
    }
}
