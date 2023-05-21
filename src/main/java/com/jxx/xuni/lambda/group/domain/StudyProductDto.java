package com.jxx.xuni.lambda.group.domain;

import lombok.Getter;

@Getter
public class StudyProductDto {
    private final Long chapterId;
    private final String title;

    public StudyProductDto(Long chapterId, String title) {
        this.chapterId = chapterId;
        this.title = title;
    }
}
