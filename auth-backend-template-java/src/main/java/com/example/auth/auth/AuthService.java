package com.example.auth.auth;

import com.example.auth.auth.dto.*;
import com.example.auth.session.Session;
import com.example.auth.session.SessionRepository;
import com.example.auth.user.User;
import com.example.auth.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User user = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .name(req.name())
                .build();

        user = userRepository.save(user);
        return toUserResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletRequest httpReq) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String accessToken = jwtService.issueAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.issueRefreshToken(user.getId());

        Session session = Session.builder()
                .user(user)
                .refreshTokenHash(passwordEncoder.encode(refreshToken))
                .userAgent(httpReq.getHeader("User-Agent"))
                .ipAddress(httpReq.getRemoteAddr())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshExpSeconds()))
                .build();
        sessionRepository.save(session);

        return new AuthResponse(accessToken, refreshToken, toUserResponse(user));
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");
        }

        jwtService.parseRefreshSubject(refreshToken); // validate signature/exp

        for (Session session : sessionRepository.findByRevokedAtIsNullAndExpiresAtAfter(Instant.now())) {
            if (!passwordEncoder.matches(refreshToken, session.getRefreshTokenHash())) continue;

            User user = session.getUser();
            String accessToken = jwtService.issueAccessToken(user.getId(), user.getEmail());
            return new TokenResponse(accessToken);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;

        for (Session session : sessionRepository.findByRevokedAtIsNullAndExpiresAtAfter(Instant.now())) {
            if (!passwordEncoder.matches(refreshToken, session.getRefreshTokenHash())) continue;
            session.setRevokedAt(Instant.now());
            sessionRepository.save(session);
            return;
        }
    }

    @Transactional(readOnly = true)
    public UserResponse me(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String token = bearerToken.substring(7).trim();
        Claims claims = jwtService.parseAccess(token);
        String userId = claims.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
