package com.jxx.xuni.lambda.group.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_task")
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_task_pk")
    private Long id;
    @Column(name = "group_member_id")
    private Long memberId;
    private Long chapterId;
    private String title;
    private boolean isDone;

    private Task(Long memberId, Long chapterId, String title, boolean isDone) {
        this.memberId = memberId;
        this.chapterId = chapterId;
        this.title = title;
        this.isDone = isDone;
    }

    public static Task init(Long memberId, Long chapterId, String title) {
        return new Task(memberId, chapterId, title, false);
    }

    protected void updateDone() {
        isDone = !isDone;
    }

    public boolean isSameChapter(Long chapterId) {
        return this.chapterId.equals(chapterId);
    }

    public boolean isSameMember(Long memberId) {
        return this.memberId.equals(memberId);
    }
}