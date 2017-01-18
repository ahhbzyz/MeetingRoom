package com.badoo.meetingroom.domain.entity.impl;


import com.badoo.meetingroom.domain.entity.intf.LocalEvent;

/**
 * Created by zhangyaozhong on 17/12/2016.
 */

public class LocalEventImpl implements LocalEvent {

    private String id;
    private int status;
    private long startTime;
    private long endTime;

    private String creatorId;
    private String creatorEmailAddress;

    private boolean isFastBook;

    public LocalEventImpl() {
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
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
    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String getCreatorEmailAddress() {
        return creatorEmailAddress;
    }

    @Override
    public void setCreatorEmailAddress(String creatorEmailAddress) {
        this.creatorEmailAddress = creatorEmailAddress;
    }

    @Override
    public boolean isFastBook() {
        return isFastBook;
    }

    @Override
    public void setFastBook(boolean isFastBook) {
        this.isFastBook = isFastBook;
    }
}
