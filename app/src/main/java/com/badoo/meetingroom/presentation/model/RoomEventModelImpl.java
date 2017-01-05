package com.badoo.meetingroom.presentation.model;

import android.graphics.Color;

import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

/**
 * Created by zhangyaozhong on 17/12/2016.
 */

public class RoomEventModelImpl implements RoomEventModel {

    public static final int AVAILABLE = 0;
    public static final int BUSY = 1;

    private String id;
    private int status;
    private long startTime;
    private long endTime;
    private String organizer;
    private boolean isOnHold;
    private boolean doNotDisturb;

    public RoomEventModelImpl() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getOrganizer() {
        return organizer;
    }

    @Override
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        if (status == BUSY) {
            isOnHold = true;
        }
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
    public long getDuration() {
        return endTime - startTime;
    }

    @Override
    public long getRemainingTime() {
        if (isExpired()) {
            return 0;
        }
        return startTime > TimeHelper.getCurrentTimeInMillis() ? getDuration() : endTime - TimeHelper.getCurrentTimeInMillis();
    }

    @Override
    public boolean isBusy() {
        return status == BUSY;
    }

    @Override
    public boolean isConfirmed(){
        return status != AVAILABLE && !isDoNotDisturb() && !isOnHold();
    }

    @Override
    public boolean isAvailable() {
        return status == AVAILABLE;
    }

    @Override
    public boolean isOnHold() {
        return isOnHold;
    }

    @Override
    public void setOnHold(boolean onHold) {
        isOnHold = onHold;
    }

    @Override
    public boolean isDoNotDisturb() {
        return doNotDisturb;
    }

    @Override
    public void setDoNotDisturb(boolean doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }

    @Override
    public int getEventColor() {
        switch (status) {
            case RoomEventModelImpl.AVAILABLE:
                return RoomEventColor.AVAILABLE_COLOR;
            case RoomEventModelImpl.BUSY:
                return (isOnHold && isProcessing())? RoomEventColor.ON_HOLD_COLOR : RoomEventColor.BUSY_COLOR;
            default:
                return RoomEventColor.EXPIRED_COLOR;
        }
    }

    @Override
    public int getEventBgColor() {
        switch (status) {
            case RoomEventModelImpl.AVAILABLE:
                return RoomEventColor.AVAILABLE_COLOR;
            case RoomEventModelImpl.BUSY:
                return (isOnHold && isProcessing()) ? RoomEventColor.ON_HOLD_BG_COLOR : RoomEventColor.BUSY_BG_COLOR;
            default:
                return RoomEventColor.EXPIRED_COLOR;
        }
    }

    @Override
    public final int getAvailableColor() {
        return RoomEventColor.AVAILABLE_COLOR;
    }

    @Override
    public final int getBusyColor() {
        return RoomEventColor.BUSY_COLOR;
    }

    @Override
    public final int getBusyBgColor() {
        return RoomEventColor.BUSY_BG_COLOR;
    }

    @Override
    public final int getEventExpiredColor() {
        return RoomEventColor.EXPIRED_COLOR;
    }

    @Override
    public boolean isExpired(){
        return endTime < TimeHelper.getCurrentTimeInMillis();
    }

    @Override
    public boolean isProcessing() {
        return startTime <= TimeHelper.getCurrentTimeInMillis() && endTime >= TimeHelper.getCurrentTimeInMillis();
    }

    @Override
    public boolean isComing() {
        return startTime > TimeHelper.getCurrentTimeInMillis();
    }

    @Override
    public String getDurationInText() {
        return getStartTimeInText() + " - " + getEndTimeInText();
    }

    @Override
    public String getStartTimeInText() {
        return TimeHelper.formatTime(getStartTime());
    }

    @Override
    public String getEndTimeInText() {
        return TimeHelper.isMidNight(getEndTime()) ? "24:00" : TimeHelper.formatTime(getEndTime());
    }

    private static class RoomEventColor {

        private static final int AVAILABLE_COLOR = Color.parseColor("#69E27E");

        private static final int BUSY_COLOR = Color.parseColor("#F5584F");
        private static final int BUSY_BG_COLOR = Color.parseColor("#FFE8E8");

        private static final int ON_HOLD_COLOR = Color.parseColor("#FFB000");
        private static final int ON_HOLD_BG_COLOR = Color.parseColor("#FFF2DB");

        private static final int EXPIRED_COLOR = Color.parseColor("#D4D4D4");

    }
}