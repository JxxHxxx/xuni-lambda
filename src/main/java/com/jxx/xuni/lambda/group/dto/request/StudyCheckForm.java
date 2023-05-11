package com.jxx.xuni.lambda.group.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyCheckForm {
    private Long chapterId;
    private String title;

    public StudyCheckForm(Long chapterId, String title) {
        this.chapterId = chapterId;
        this.title = title;
    }
}
