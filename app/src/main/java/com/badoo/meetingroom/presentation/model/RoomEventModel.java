package com.badoo.meetingroom.presentation.model;

import android.graphics.Color;

import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

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

    public RoomEventModel() {}

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
        return startTime > TimeHelper.getCurrentTimeInMillis() ? getDuration() : endTime - TimeHelper.getCurrentTimeInMillis();
    }

    public boolean isBusy() {
        return status == BUSY;
    }

    public boolean isConfirmed (){
        return status != AVAILABLE && !isDoNotDisturb() && !isOnHold();
    }

    public boolean isAvailable() {
        return status == AVAILABLE;
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
            case RoomEventModel.AVAILABLE:
                return RoomEventColor.AVAILABLE_COLOR;
            case RoomEventModel.BUSY:
                return isOnHold ? RoomEventColor.ON_HOLD_COLOR : RoomEventColor.BUSY_COLOR;
            default:
                return RoomEventColor.EXPIRED_COLOR;
        }
    }

    public int getEventBgColor() {
        switch (status) {
            case RoomEventModel.AVAILABLE:
                return RoomEventColor.AVAILABLE_COLOR;
            case RoomEventModel.BUSY:
                return isOnHold ? RoomEventColor.ON_HOLD_BG_COLOR : RoomEventColor.BUSY_BG_COLOR;
            default:
                return RoomEventColor.EXPIRED_COLOR;
        }
    }

    public final int getAvailableColor() {
        return RoomEventColor.AVAILABLE_COLOR;
    }

    public final int getBusyColor() {
        return RoomEventColor.BUSY_COLOR;
    }

    public final int getBusyBgColor() {
        return RoomEventColor.BUSY_BG_COLOR;
    }

    public final int getEventExpiredColor() {
        return RoomEventColor.EXPIRED_COLOR;
    }

    public boolean isExpired(){
        return endTime < TimeHelper.getCurrentTimeInMillis();
    }

    public boolean isProcessing() {
        return startTime <= TimeHelper.getCurrentTimeInMillis() && endTime >= TimeHelper.getCurrentTimeInMillis();
    }

    public boolean isComing() {
        return startTime > TimeHelper.getCurrentTimeInMillis();
    }

    public String getPeriod() {
        return getStartTimeInText() + " - " + getEndTimeInText();
    }

    public String getStartTimeInText() {
        return TimeHelper.formatTime(getStartTime());
    }

    public String getEndTimeInText() {
        return TimeHelper.formatTime(getEndTime());
    }

    private static class RoomEventColor {

        private static final int AVAILABLE_COLOR = Color.parseColor("#69E27E");
        private static final int AVAILABLE_BG_COLOR = Color.parseColor("#69E27E");

        private static final int BUSY_COLOR = Color.parseColor("#F5584F");
        private static final int BUSY_BG_COLOR = Color.parseColor("#FFE8E8");

        private static final int ON_HOLD_COLOR = Color.parseColor("#FFB000");
        private static final int ON_HOLD_BG_COLOR = Color.parseColor("#FFF2DB");

        private static final int EXPIRED_COLOR = Color.parseColor("#D4D4D4");

    }
}