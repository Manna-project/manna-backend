package com.manna.friend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_group_members", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_group_friend",
                columnNames = {"friend_group_id", "friend_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_group_member_id")
    private Integer friendGroupMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_group_id", nullable = false)
    private FriendGroup friendGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public FriendGroupMember(FriendGroup friendGroup, Friend friend, LocalDateTime createdAt) {
        this.friendGroup = friendGroup;
        this.friend = friend;
        this.createdAt = createdAt;
    }
}
