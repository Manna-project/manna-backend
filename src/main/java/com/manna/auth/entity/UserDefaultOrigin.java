package com.manna.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_default_origins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDefaultOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Integer originId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 300)
    private String originNameKr;

    @Column(length = 500)
    private String originNameEn;

    @Column(precision = 10, scale = 7)
    private BigDecimal originLat;

    @Column(precision = 10, scale = 7)
    private BigDecimal originLng;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder

    public UserDefaultOrigin(Integer originId, User user, String originNameKr, String originNameEn, BigDecimal originLat, BigDecimal originLng, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.originId = originId;
        this.user = user;
        this.originNameKr = originNameKr;
        this.originNameEn = originNameEn;
        this.originLat = originLat;
        this.originLng = originLng;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
