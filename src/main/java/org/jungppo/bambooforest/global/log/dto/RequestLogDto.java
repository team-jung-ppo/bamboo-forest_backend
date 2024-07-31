package org.jungppo.bambooforest.global.log.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RequestLogDto {

    private String requestMethod;
    private String requestUri;
    private String requestIp;
    private Long queryCounts = 0L;
    private Long queryTime = 0L;

    public void setRequestDetails(String requestMethod, String requestUri, String requestIp) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.requestIp = requestIp;
    }

    public void addQueryCount() {
        queryCounts++;
    }

    public void addQueryTime(final Long queryTime) {
        this.queryTime += queryTime;
    }
}
