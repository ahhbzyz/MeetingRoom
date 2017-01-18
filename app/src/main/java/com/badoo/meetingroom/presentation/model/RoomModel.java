package com.badoo.meetingroom.presentation.model;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */
public interface RoomModel {

    String getId();

    void setId(String id);

    EventModel getCurrentEvent();

    void setCurrentEvent(EventModel currentEvent);

    String getName();

    void setName(String name);

    int getFloor();

    void setFloor(int floor);

    int getStatus();

    void setStatus(int status);

    int getCapacity();

    void setCapacity(int capacity);

    boolean isTvSupported();

    void setTvSupported(boolean tvSupported);

    boolean isVideoConferenceSupported();

    void setVideoConferenceSupported(boolean videoConferenceSupported);

    boolean isBeverageAllowed();

    void setBeverageAllowed(boolean beverageAllowed);

    boolean isStationerySupported();

    void setStationerySupported(boolean stationerySupported);
}
