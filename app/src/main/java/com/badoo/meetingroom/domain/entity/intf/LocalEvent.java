package com.badoo.meetingroom.domain.entity.intf;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */
public interface LocalEvent extends Entity {
    int AVAILABLE = 0;
    int BUSY = 1;

    void setId(String id);

    String getId();

    int getStatus();

    void setStatus(int status);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    String getCreatorId();

    void setCreatorId(String creatorId);

    String getCreatorEmailAddress();

    void setCreatorEmailAddress(String creatorEmailAddress);

    boolean isFastBook();

    void setFastBook(boolean isFastBook);
}
