package com.staysmart.user.repository;

import com.staysmart.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user.id = :userId and rt.revoked = false")
    void revokeAllForUser(Long userId);
}
