package com.lawfirm.api.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.junit.jupiter.api.Assertions.*;

class AuthCookieServiceTest {

    @Test
    @DisplayName("createAuthCookie: Debería generar cookie Host-Only sin atributo domain y con atributos seguros")
    void shouldCreateHostOnlyAuthCookie() {
        AuthCookieService cookieService = new AuthCookieService("lawfirm_token", 3600000, true);

        ResponseCookie cookie = cookieService.createAuthCookie("sample.jwt.token");

        assertEquals("lawfirm_token", cookie.getName());
        assertEquals("sample.jwt.token", cookie.getValue());
        assertNull(cookie.getDomain(), "El atributo domain DEBE ser null para permitir cookie Host-Only");
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
        assertEquals(3600, cookie.getMaxAge().getSeconds());
    }

    @Test
    @DisplayName("createLogoutCookie: Debería generar cookie vacía con maxAge(0) y atributos consistentes")
    void shouldCreateConsistentLogoutCookie() {
        AuthCookieService cookieService = new AuthCookieService("lawfirm_token", 3600000, false);

        ResponseCookie cookie = cookieService.createLogoutCookie();

        assertEquals("lawfirm_token", cookie.getName());
        assertEquals("", cookie.getValue());
        assertNull(cookie.getDomain(), "El atributo domain DEBE ser null para logout Host-Only");
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }
}
