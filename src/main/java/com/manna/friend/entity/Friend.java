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
@Table(name = "friends", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_requester_receiver",
                columnNames = {"requester_user_id", "receiver_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Integer friendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendNickname> nicknames = new ArrayList<>();

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendGroupMember> groupMembers = new ArrayList<>();

    @Builder
    public Friend(User requester, User receiver, FriendStatus status, LocalDateTime requestedAt) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = status;
        this.requestedAt = requestedAt;
    }
}
