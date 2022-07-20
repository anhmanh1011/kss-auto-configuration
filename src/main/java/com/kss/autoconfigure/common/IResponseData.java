package com.kss.autoconfigure.common;

public interface IResponseData {

    String getTime();

    Object getData();

    int getCode();

    String getMessage();

    public String getTraceId();

    public void setTraceId(String traceId);
}
