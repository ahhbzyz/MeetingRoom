package com.badoo.meetingroom.presentation.model;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */
public interface RoomEventModel {

    int AVAILABLE = 0;
    int BUSY = 1;

    String getId();

    void setId(String id);

    String getOrganizer();

    void setOrganizer(String organizer);

    int getStatus();

    void setStatus(int status);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    long getDuration();

    long getRemainingTime();

    boolean isBusy();

    boolean isConfirmed();

    boolean isAvailable();

    boolean isOnHold();

    void setOnHold(boolean onHold);

    boolean isDoNotDisturb();

    void setDoNotDisturb(boolean doNotDisturb);

    int getEventColor();

    int getEventBgColor();

    int getAvailableColor();

    int getBusyColor();

    int getBusyBgColor();

    int getEventExpiredColor();

    boolean isExpired();

    boolean isProcessing();

    boolean isComing();

    String getDurationInText();

    String getStartTimeInText();

    String getEndTimeInText();
}
