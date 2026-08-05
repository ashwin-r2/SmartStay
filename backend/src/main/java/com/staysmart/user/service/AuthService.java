package com.staysmart.user.service;

import com.staysmart.config.AppProperties;
import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ConflictException;
import com.staysmart.exception.UnauthorizedException;
import com.staysmart.security.JwtService;
import com.staysmart.user.dto.AuthResponse;
import com.staysmart.user.dto.LoginRequest;
import com.staysmart.user.dto.RegisterRequest;
import com.staysmart.user.dto.UserResponse;
import com.staysmart.user.entity.RefreshToken;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.repository.RefreshTokenRepository;
import com.staysmart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

/**
 * Handles registration, credential-based login, refresh-token rotation and logout. Access tokens
 * are short-lived stateless JWTs; refresh tokens are also JWTs but their hash is persisted so they
 * can be revoked (logout, password change) — a plain JWT alone cannot be invalidated server-side.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("Cannot self-register as ADMIN");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(request.role() != null ? request.role() : Role.USER)
                .enabled(true)
                .build();
        User saved = userRepository.save(user);
        log.info("Registered new {} user: {}", saved.getRole(), saved.getEmail());

        return issueTokens(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password()));
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("This account has been disabled. Contact support.");
        }

        log.info("User {} logged in", user.getEmail());
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        String tokenHash = hash(refreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        if (!"refresh".equals(jwtService.extractTokenType(refreshToken)) || jwtService.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        User user = stored.getUser();
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        log.info("Refreshed tokens for user {}", user.getEmail());
        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(hash(refreshToken))
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user, user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user, user.getId());

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(hash(refreshToken))
                .expiresAt(Instant.now().plus(appProperties.getJwt().getRefreshTokenExpirationMs(), ChronoUnit.MILLIS))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);

        return AuthResponse.of(accessToken, refreshToken, appProperties.getJwt().getAccessTokenExpirationMs(),
                UserResponse.from(user));
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
