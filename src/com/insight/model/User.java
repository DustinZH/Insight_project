package com.insight.model;

import java.util.Set;

/**
 * Created by dustin on 2018/4/3.
 */
public class User {
    private String ipv4;
    private String firstCheckTime;
    private long firstLongTypeTime;
    private String lastCheckTime;
    private long expirationTime;
    private Set<String> documents;
    private int count;

    public long getFirstLongTypeTime() {
        return firstLongTypeTime;
    }

    public User(String ipv4, String startTime, long firstLongTypeTime, long expiration, Set<String> documents, int count) {
        this.ipv4 = ipv4;
        this.firstCheckTime = startTime;
        this.firstLongTypeTime = firstLongTypeTime;

        this.lastCheckTime = startTime;
        this.documents = documents;
        this.expirationTime = expiration;
        this.count = count;
    }

    public void setLastCheckTime(String lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void updateCount(int n){
        this.count += n;
    }

    public String getIpv4() {
        return ipv4;

    }

    public String getFirstCheckTime() {
        return firstCheckTime;
    }

    public String getLastCheckTime() {
        return lastCheckTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public Set<String> getDocuments() {
        return documents;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "User{" +
                "ipv4='" + ipv4 + '\'' +
                ", firstCheckTime='" + firstCheckTime + '\'' +
                ", lastCheckTime='" + lastCheckTime + '\'' +
                ", expirationTime=" + expirationTime +
                ", documents=" + documents +
                ", count=" + count +
                '}';
    }
}
