package org.jungppo.bambooforest.global.log.dto;

import lombok.Getter;

@Getter
public class RequestLogDto {

    private String requestMethod;
    private String requestUri;
    private String requestIp;
    private Long queryCounts = 0L;
    private Long queryTime = 0L;

    public void setRequestDetails(final String requestMethod, final String requestUri, final String requestIp) {
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

    @Override
    public String toString() {
        return String.format(
                "RequestLog: method='%s', uri='%s', ip='%s', queryCounts=%d, queryTime=%d",
                requestMethod, requestUri, requestIp, queryCounts, queryTime
        );
    }
}
