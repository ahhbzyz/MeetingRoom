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
    private String eventTitle;
    private String creatorId;
    private String creatorName;
    private String creatorEmailAddress;
    private boolean isFastBooking;

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
    public String getEventTitle() {
        return eventTitle;
    }

    @Override
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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
    public String getCreatorName() {
        return creatorName;
    }

    @Override
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    @Override
    public String getCreatorEmailAddress() {
        return creatorEmailAddress;
    }

    @Override
    public void setCreatorEmailAddress(String creatorEmailAddress) {
        this.creatorEmailAddress = creatorEmailAddress;
    }

    public boolean isFastBooking() {
        return isFastBooking;
    }

    public void setFastBooking(boolean isFastBooking) {
        this.isFastBooking = isFastBooking;
    }
}
