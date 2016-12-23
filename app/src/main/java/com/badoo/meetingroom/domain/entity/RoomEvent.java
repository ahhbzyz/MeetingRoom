package com.badoo.meetingroom.domain.entity;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */
public interface RoomEvent extends Entity {

    int getStatus();

    void setStatus(int status);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    String getOrganizer();

    void setOrganizer(String organizer);
}
