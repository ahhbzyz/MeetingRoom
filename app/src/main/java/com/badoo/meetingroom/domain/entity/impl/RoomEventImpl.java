package com.badoo.meetingroom.domain.entity.impl;


import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

/**
 * Created by zhangyaozhong on 17/12/2016.
 */

public class RoomEventImpl implements RoomEvent {

    public static final int AVAILABLE = 0;
    public static final int BUSY = 1;

    private int status;
    private long startTime;
    private long endTime;
    private String organizer;
    private GoogleCredential credential;

    public RoomEventImpl() {
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getOrganizer() {
        return organizer;
    }

    @Override
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public GoogleCredential getCredential() {
        return credential;
    }

    public void setCredential(GoogleCredential credential) {
        this.credential = credential;
    }
}