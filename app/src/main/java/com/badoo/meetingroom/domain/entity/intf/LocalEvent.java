package com.badoo.meetingroom.domain.entity.intf;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */
public interface LocalEvent extends Entity {
    int AVAILABLE = 0;
    int BUSY = 1;
    String FAST_BOOKING_DESCRIPTION = "Fast booking";

    void setId(String id);

    String getId();

    int getStatus();

    void setStatus(int status);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    String getEventTitle();

    void setEventTitle(String eventTitle);

    String getCreatorId();

    void setCreatorId(String creatorId);

    String getCreatorName();

    void setCreatorName(String creatorName);

    String getCreatorEmailAddress();

    void setCreatorEmailAddress(String creatorEmailAddress);

    boolean isFastBooking();

    void setFastBooking(boolean isFastBooking);
}
