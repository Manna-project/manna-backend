package com.manna.auth.repository;

import com.manna.auth.entity.RefreshSession;
import com.manna.auth.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select session
        from RefreshSession session
        join fetch session.user
        where session.sessionId = :sessionId
        """)
    Optional<RefreshSession> findBySessionIdForUpdate(
        @Param("sessionId") UUID sessionId
    );

    List<RefreshSession> findAllByUserAndRevokedAtIsNull(User user);
}
