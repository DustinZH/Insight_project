package com.insight.model;

/**
 * Created by dustin on 2018/4/3.
 */
public class LogEntry {
    private String ip;
    private String time;
    private String document;

    public LogEntry(String ip, String time, String document){
        this.ip = ip;
        this.time = time;
        this.document = document;
    }

    public String getIp() {
        return ip;
    }

    public String getDocument() {
        return document;
    }

    public String getTime() {

        return time;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ip='" + ip + '\'' +
                ", time='" + time + '\'' +
                ", document='" + document + '\'' +
                '}';
    }
}
