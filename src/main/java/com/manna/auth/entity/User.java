package com.manna.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_logintype_providerid",
                        columnNames = {"login_type"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Integer userId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false, length = 100)
    private String providerId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column()
    private String nickname;

    @Column()
    private String profileImage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column()
    private LocalDateTime deletedAt;

    @Column(nullable = false, updatable = false, unique = true)
    private UUID entityId = UUID.randomUUID();

    @Builder
    public User(LoginType loginType, String providerId, String email, String name, String nickname, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, UUID entityId) {
        this.loginType = loginType;
        this.providerId = providerId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.entityId = entityId;
    }
}
