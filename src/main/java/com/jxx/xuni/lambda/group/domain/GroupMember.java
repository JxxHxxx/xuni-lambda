package com.jxx.xuni.lambda.group.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_member")
public class GroupMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_pk")
    private Long id;
    private Long groupMemberId;
    private String groupMemberName;
    private Boolean isLeft;
    private LocalDateTime lastVisitedTime;
    @JoinColumn(name = "group_id")
    @ManyToOne
    private Group group;

    public GroupMember(Long groupMemberId, String groupMemberName, Group group) {
        this.groupMemberId = groupMemberId;
        this.groupMemberName = groupMemberName;
        this.isLeft = false;
        this.lastVisitedTime = now();
        this.group = group;
    }
}
