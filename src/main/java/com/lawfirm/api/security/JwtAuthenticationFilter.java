package com.lawfirm.api.security;

import com.lawfirm.api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthCookieService authCookieService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            AuthCookieService authCookieService
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authCookieService = authCookieService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtService.isTokenValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String email = jwtService.extractEmail(token);
            userRepository.findByEmailWithPerson(email).ifPresent(user -> {
                if (user.isEnabled() && user.isAccountNonLocked()) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            });
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // 1. Prioridad: Cookie HttpOnly ("lawfirm_token") enviada en las peticiones del navegador/proxy
        if (request.getCookies() != null) {
            var cookieToken = Arrays.stream(request.getCookies())
                    .filter(c -> authCookieService.getCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
            if (cookieToken.isPresent()) {
                return cookieToken.get();
            }
        }
        // 2. Fallback: Header Authorization Bearer (útil para tests automatizados, Postman o Swagger)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
