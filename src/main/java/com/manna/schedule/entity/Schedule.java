package com.manna.schedule.entity;

import com.manna.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "title")
    private String title;

    @Column(name = "memo")
    private String memo;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_lat", precision = 10, scale = 7)
    private BigDecimal locationLat;

    @Column(name = "location_lng", precision = 10, scale = 7)
    private BigDecimal locationLng;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Schedule(User createdBy, String title, String memo, String locationName, BigDecimal locationLat, BigDecimal locationLng, LocalDateTime scheduledAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdBy = createdBy;
        this.title = title;
        this.memo = memo;
        this.locationName = locationName;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.scheduledAt = scheduledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
