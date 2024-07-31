package org.jungppo.bambooforest.global.log.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoggingForm {

    private Long queryCounts = 0L;
    private Long queryTime = 0L;

    public void queryCountUp() {
        queryCounts++;
    }

    public void addQueryTime(final Long queryTime) {
        this.queryTime += queryTime;
    }
}
