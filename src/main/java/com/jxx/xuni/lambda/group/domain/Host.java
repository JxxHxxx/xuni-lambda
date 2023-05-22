package com.jxx.xuni.lambda.group.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Host {
    private Long hostId;
    private String hostName;

    public Host(Long hostId, String hostName) {
        this.hostId = hostId;
        this.hostName = hostName;
    }
}
