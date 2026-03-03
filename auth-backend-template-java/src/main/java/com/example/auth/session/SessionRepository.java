package com.example.auth.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByRevokedAtIsNullAndExpiresAtAfter(Instant now);
}
