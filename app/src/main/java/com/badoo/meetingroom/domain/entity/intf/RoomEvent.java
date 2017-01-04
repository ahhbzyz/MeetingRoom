package com.badoo.meetingroom.domain.entity.intf;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */
public interface RoomEvent extends Entity {
    void setId(String id);
    String getId();
    int getStatus();
    void setStatus(int status);
    long getStartTime();
    void setStartTime(long startTime);
    long getEndTime();
    void setEndTime(long endTime);
    String getOrganizer();
    void setOrganizer(String organizer);
}
