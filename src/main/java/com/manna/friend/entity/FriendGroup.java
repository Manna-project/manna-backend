package com.manna.friend.entity;

import com.manna.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "friend_groups", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_name",
                columnNames = {"user_id", "name"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Integer friendGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "friendGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendGroupMember> members = new ArrayList<>();

    @Builder
    public FriendGroup(User user, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
