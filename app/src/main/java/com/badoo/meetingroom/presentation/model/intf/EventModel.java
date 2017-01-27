package com.badoo.meetingroom.presentation.model.intf;

import android.content.Context;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhangyaozhong on 04/01/2017.
 */
public interface EventModel extends Parcelable {

    int AVAILABLE = 0;
    int BUSY = 1;
    String FAST_BOOKING_DESCRIPTION = "Fast booking";
    Creator<EventModel> CREATOR = null;

    String getId();

    void setId(String id);

    String getCreatorId();

    void setCreatorId(String creatorId);

    String getCreatorName();

    void setCreatorName(String creatorName);

    String getCreatorEmailAddress();

    void setCreatorEmailAddress(String creatorEmailAddress);

    int getStatus();

    void setStatus(int status);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    String getEventTitle();

    void setEventTitle(String eventTitle);

    long getDuration();

    long getRemainingTime();

    long getRemainingTimeUntilNextBusyEvent();

    long getNextBusyEventStartTime();

    void setNextBusyEventStartTime(long nextBusyEventStartTime);

    boolean isBusy();

    boolean isAvailable();

    boolean isOnHold();

    long getRemainingOnHoldTime();

    long getOnHoldTime();

    boolean isConfirmed();

    void setConfirmed(boolean confirmed);

    boolean isDoNotDisturb();

    void setDoNotDisturb(boolean doNotDisturb);

    boolean isFastBooking();

    void setFastBooking(boolean fastBooking);

    int getEventColor(Context context);

    int getEventBgColor(Context context);

    int getAvailableEventColor(Context context);

    int getBusyEventColor(Context context);

    int getBusyEventBgColor(Context context);

    int getOnHoldEventColor(Context context);

    int getOnHoldEventBgColor(Context context);

    int getExpiredEventColor(Context context);

    boolean isExpired();

    boolean isProcessing();

    boolean isComing();

    String getDurationInText();

    String getStartTimeInText();

    String getEndTimeInText();

    void setTimeStamps(List<Long> timeStamps);

    List<Long> getTimeStamps();
}
