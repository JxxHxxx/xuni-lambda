package com.jxx.xuni.lambda.group.domain;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jxx.xuni.lambda.group.domain.GroupStatus.GATHERING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group", indexes = @Index(name = "study_group_category", columnList = "category"))
public class Group {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private GroupStatus groupStatus;
    private LocalDateTime createdDate;
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

    @JoinColumn(name = "group_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @JoinColumn(name = "group_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @Builder
    public Group(Period period, Time time, Capacity capacity, Study study, Host host) {
        this.groupStatus = GATHERING;
        this.period = period;
        this.time = time;
        this.capacity = capacity;
        this.study = study;
        this.host = host;
        this.version = 0l;
        this.createdDate = LocalDateTime.now();

        this.groupMembers.add(new GroupMember(host.getHostId(), host.getHostName()));
        this.capacity.subtractOneLeftCapacity();

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
