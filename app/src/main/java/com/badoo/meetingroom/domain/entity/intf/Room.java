package com.badoo.meetingroom.domain.entity.intf;

import com.badoo.meetingroom.presentation.model.RoomEventModel;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */
public interface Room {
    String getName();

    void setName(String name);

    int getFloor();

    void setFloor(int floor);

    int getStatus();

    void setStatus(int status);

    RoomEventModel getCurrentEvent();

    void setCurrentEvent(RoomEventModel currentEvent);

    int getCapacity();

    void setCapacity(int capacity);

    boolean isTvSupported();

    void setTvSupported(boolean tvSupported);

    boolean isVideoConferenceSupported();

    void setVideoConferenceSupported(boolean videoConferenceSupported);

    boolean isBeverageSupported();

    void setBeverageSupported(boolean beverageSupported);

    boolean isStationerySupported();

    void setStationerySupported(boolean stationerySupported);
}
