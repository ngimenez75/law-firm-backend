package com.lawfirm.api.controller;

import com.lawfirm.api.dto.LoginRequest;
import com.lawfirm.api.dto.UserResponse;
import com.lawfirm.api.entity.User;
import com.lawfirm.api.security.AuthCookieService;
import com.lawfirm.api.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthCookieService authCookieService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuthCookieService authCookieService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) auth.getPrincipal();
        String jwt = jwtService.generateToken(user);

        ResponseCookie cookie = authCookieService.createAuthCookie(jwt);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getPerson().getFullName(),
                        user.getRole().name()
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = authCookieService.createLogoutCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPerson().getFullName(),
                user.getRole().name()
        ));
    }
}
