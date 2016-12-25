package com.badoo.meetingroom.presentation.model;

import android.graphics.Color;

import com.badoo.meetingroom.domain.entity.RoomEventImpl;
import com.badoo.meetingroom.presentation.view.timeutils.TimeUtils;

/**
 * Created by zhangyaozhong on 17/12/2016.
 */

public class RoomEventModel {

    public static final int AVAILABLE = 0;
    public static final int BUSY = 1;

    private String organizer;

    private int status;
    private long startTime;
    private long endTime;

    private boolean isOnHold;
    private boolean doNotDisturb;

    public RoomEventModel() {
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if (status == BUSY) {
            isOnHold = true;
        }
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public long getRemainingTime() {
        if (isExpired()) {
            return 0;
        }
        return startTime > TimeUtils.getCurrentTimeInMillis() ? getDuration() : endTime - TimeUtils.getCurrentTimeInMillis();
    }

    public boolean isOnHold() {
        return isOnHold;
    }

    public void setOnHold(boolean onHold) {
        isOnHold = onHold;
    }

    public boolean isDoNotDisturb() {
        return doNotDisturb;
    }

    public void setDoNotDisturb(boolean doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }

    public int getEventColor() {
        switch (status) {
            case RoomEventImpl.AVAILABLE:
                return UserEventColor.AVAILABLE_COLOR;
            case RoomEventImpl.BUSY:
                return isOnHold ? UserEventColor.ON_HOLD_COLOR : UserEventColor.BUSY_COLOR;
            default:
                return UserEventColor.EXPIRED_COLOR;
        }
    }

    public boolean isExpired(){
        return endTime < TimeUtils.getCurrentTimeInMillis();
    }

    public boolean isProcessing() {
        return startTime <= TimeUtils.getCurrentTimeInMillis() && endTime >= TimeUtils.getCurrentTimeInMillis();
    }

    public boolean isComing() {
        return startTime > TimeUtils.getCurrentTimeInMillis();
    }

    public String getPeriod() {
        return TimeUtils.formatTime(startTime) + " - " + TimeUtils.formatTime(endTime);
    }

    private static class UserEventColor {

        private static final int AVAILABLE_COLOR = Color.parseColor("#69E27E");
        private static final int BUSY_COLOR = Color.parseColor("#F5584F");
        private static final int ON_HOLD_COLOR = Color.parseColor("#FFB000");
        private static final int EXPIRED_COLOR = Color.parseColor("#D4D4D4");

        //public static final int EXPIRED_COLOR_OPACITIY_30 = Color.parseColor("#4DD4D4D4");
        //public static final int BUSY_BG_COLOR = Color.parseColor("#FFE8E8");

    }
}