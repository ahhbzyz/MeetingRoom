package com.badoo.meetingroom.presentation.model.impl;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyaozhong on 17/12/2016.
 */

public class EventModelImpl implements EventModel, Parcelable {

    private String id;
    private int status;
    private long startTime;
    private long endTime;
    private String eventTitle;
    private String creatorId;
    private String creatorName;
    private String creatorEmailAddress;
    private boolean isConfirmed;
    private boolean doNotDisturb;
    private boolean isFastBooking;
    private List<Long> timeStamps;
    private final long ON_HOLD_TIME = TimeHelper.min2Millis(5);

    public EventModelImpl() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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

    public String getCreatorEmailAddress() {
        return creatorEmailAddress;
    }

    public void setCreatorEmailAddress(String creatorEmailAddress) {
        this.creatorEmailAddress = creatorEmailAddress;
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
    public boolean isAvailable() {
        return status == AVAILABLE;
    }

    @Override
    public boolean isOnHold() {
        if (isProcessing() && isBusy() && !isConfirmed() && getDuration() > ON_HOLD_TIME) {
            if (TimeHelper.getCurrentTimeInMillis() <= getStartTime() + ON_HOLD_TIME) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getRemainingOnHoldTime() {
        return isOnHold() ? getStartTime() + getOnHoldTime() - TimeHelper.getCurrentTimeInMillis() : 0;
    }

    @Override
    public long getOnHoldTime() {
        return ON_HOLD_TIME;
    }

    @Override
    public boolean isConfirmed(){
        return isConfirmed;
    }

    @Override
    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
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
    public boolean isFastBooking() {
        return isFastBooking;
    }

    @Override
    public void setFastBooking(boolean fastBooking) {
        isFastBooking = fastBooking;
    }

    @Override
    public int getEventColor() {
        switch (status) {
            case AVAILABLE:
                return RoomEventColor.AVAILABLE_COLOR;
            case BUSY:
                return (isOnHold() && isProcessing())? RoomEventColor.ON_HOLD_COLOR : RoomEventColor.BUSY_COLOR;
            default:
                return RoomEventColor.EXPIRED_COLOR;
        }
    }

    @Override
    public int getEventBgColor() {
        switch (status) {
            case AVAILABLE:
                return RoomEventColor.AVAILABLE_COLOR;
            case BUSY:
                return (isOnHold() && isProcessing()) ? RoomEventColor.ON_HOLD_BG_COLOR : RoomEventColor.BUSY_BG_COLOR;
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

    @Override
    public void setTimeStamps(List<Long> timeStamps) {
        this.timeStamps = timeStamps;
    }

    @Override
    public List<Long> getTimeStamps() {
        return timeStamps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.id);
        out.writeInt(this.status);
        out.writeLong(this.startTime);
        out.writeLong(this.endTime);
        out.writeString(this.eventTitle);
        out.writeString(this.creatorId);
        out.writeString(this.creatorName);
        out.writeString(this.creatorEmailAddress);
        out.writeByte(this.isConfirmed ? (byte) 1 : (byte) 0);
        out.writeByte(this.doNotDisturb ? (byte) 1 : (byte) 0);
        out.writeByte(this.isFastBooking ? (byte) 1 : (byte) 0);
        out.writeList(this.timeStamps);
    }

    private EventModelImpl(Parcel in) {
        this.id = in.readString();
        this.status = in.readInt();
        this.startTime = in.readLong();
        this.endTime = in.readLong();
        this.eventTitle = in.readString();
        this.creatorName = in.readString();
        this.creatorId = in.readString();
        this.creatorEmailAddress = in.readString();
        this.isConfirmed = in.readByte() != 0;
        this.doNotDisturb = in.readByte() != 0;
        this.isFastBooking = in.readByte() != 0;
        this.timeStamps = new ArrayList<>();
        in.readList(this.timeStamps, Long.class.getClassLoader());
    }

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModelImpl createFromParcel(Parcel source) {
            return new EventModelImpl(source);
        }

        @Override
        public EventModelImpl[] newArray(int size) {
            return new EventModelImpl[size];
        }
    };


    private static class RoomEventColor {

        private static final int AVAILABLE_COLOR = Color.parseColor("#69E27E");

        private static final int BUSY_COLOR = Color.parseColor("#F5584F");
        private static final int BUSY_BG_COLOR = Color.parseColor("#26F5584F");

        private static final int ON_HOLD_COLOR = Color.parseColor("#FFB000");
        private static final int ON_HOLD_BG_COLOR = Color.parseColor("#26FFB000");

        private static final int EXPIRED_COLOR = Color.parseColor("#D4D4D4");

    }
}