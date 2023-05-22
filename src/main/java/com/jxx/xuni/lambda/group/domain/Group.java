package com.jxx.xuni.lambda.group.domain;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProductDetail;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.xuni.lambda.group.domain.GroupStatus.GATHERING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "group")
    private List<GroupMember> groupMembers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "group")
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

        this.groupMembers.add(new GroupMember(host.getHostId(), host.getHostName(), this));
        this.capacity.subtractOneLeftCapacity();
    }

    public void initGroupTask(Long memberId, List<StudyProductDetail> details) {
        List<Task> tasks = details.stream()
                .map(d -> Task.init(memberId, d.getChapterId(), d.getTitle(), this))
                .toList();

        this.tasks.addAll(tasks);
    }

    public GroupStatus getGroupStatus() {
        return groupStatus;
    }

    public String getStudyProductId() {
        return this.study.getId();
    }

    public Long getId() {
        return id;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public LocalDate getStartDate() {
        return period.getStartDate();
    }

    public LocalDate getEndDate() {
        return period.getEndDate();
    }

    public void changeGroupStatusTo(GroupStatus groupStatus) {
        this.groupStatus = groupStatus;
    }
}
