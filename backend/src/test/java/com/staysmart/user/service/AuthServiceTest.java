package com.staysmart.user.service;

import com.staysmart.config.AppProperties;
import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ConflictException;
import com.staysmart.exception.UnauthorizedException;
import com.staysmart.security.JwtService;
import com.staysmart.user.dto.AuthResponse;
import com.staysmart.user.dto.LoginRequest;
import com.staysmart.user.dto.RegisterRequest;
import com.staysmart.user.entity.RefreshToken;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.repository.RefreshTokenRepository;
import com.staysmart.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AppProperties appProperties;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
        appProperties.getJwt().setSecret("test-secret-key-for-junit-tests-min-32-bytes-long");
        appProperties.getJwt().setAccessTokenExpirationMs(3_600_000L);
        appProperties.getJwt().setRefreshTokenExpirationMs(1_209_600_000L);

        authService = new AuthService(userRepository, refreshTokenRepository, passwordEncoder, jwtService,
                authenticationManager, appProperties);
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password123", "555-1234", null);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateAccessToken(any(), eq(1L), anyString())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(), eq(1L))).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.user().email()).isEqualTo("jane@example.com");
        assertThat(response.user().role()).isEqualTo(Role.USER);

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().isRevoked()).isFalse();
    }

    @Test
    void register_rejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password123", null, null);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(ConflictException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_rejectsSelfRegisterAsAdmin() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password123", null, Role.ADMIN);

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(BadRequestException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_rejectsBadCredentials() {
        LoginRequest request = new LoginRequest("jane@example.com", "wrong-password");
        doThrow(new BadCredentialsException("bad")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_rejectsDisabledAccount() {
        LoginRequest request = new LoginRequest("jane@example.com", "Password123");
        User disabledUser = User.builder().id(1L).email("jane@example.com").fullName("Jane")
                .passwordHash("hashed").role(Role.USER).enabled(false).build();
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(disabledUser));

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(UnauthorizedException.class);
    }
}
