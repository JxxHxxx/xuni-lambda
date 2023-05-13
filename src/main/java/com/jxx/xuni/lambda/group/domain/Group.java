package com.jxx.xuni.lambda.group.domain;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group", indexes = @Index(name = "study_group_category", columnList = "category"))
public class Group {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private GroupStatus groupStatus;
    @Embedded
    private Period period;
    @Embedded
    private Time time;
    @Embedded
    private Capacity capacity;
    @Embedded
    private Study study;
    @Embedded
    private Host host;
    @Version
    private long version;

    @ElementCollection
    @CollectionTable(name = "group_member", joinColumns = @JoinColumn(name = "group_id"))
    private List<GroupMember> groupMembers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "group_study_check", joinColumns = @JoinColumn(name = "group_id"))
    private List<StudyCheck> studyChecks = new ArrayList<>();

    @Builder
    protected Group(Long id, GroupStatus groupStatus, Period period, Time time, Capacity capacity, Study study, Host host, List<GroupMember> groupMembers) {
        this.id = id;
        this.groupStatus = groupStatus;
        this.period = period;
        this.time = time;
        this.capacity = capacity;
        this.study = study;
        this.host = host;
        this.groupMembers = groupMembers;

        addInGroup(new GroupMember(host.getHostId(), host.getHostName()));
    }

    private void addInGroup(GroupMember member) {
        Optional<GroupMember> optionalGroupMember = groupMembers.stream().filter(g -> g.isLeftMember(member)).findFirst();
        if (optionalGroupMember.isPresent()) {
            optionalGroupMember.get().comeBack();
        }
        if (optionalGroupMember.isEmpty()) {
            groupMembers.add(member);
        }

        this.capacity.subtractOneLeftCapacity();
    }
}
