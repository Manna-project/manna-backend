package com.manna.schedule.entity;

import com.manna.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column()
    private String title;

    @Column()
    private String memo;

    @Column()
    private String locationName;

    @Column(precision = 10, scale = 7)
    private BigDecimal locationLat;

    @Column(precision = 10, scale = 7)
    private BigDecimal locationLng;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleParticipant> participants = new ArrayList<>();

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
