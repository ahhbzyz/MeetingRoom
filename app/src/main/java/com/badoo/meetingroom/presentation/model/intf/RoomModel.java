package com.badoo.meetingroom.presentation.model.intf;

import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */
public interface RoomModel extends Parcelable {

    String getId();

    void setId(String id);

    List<EventModel> getEventModelList();

    void setEventModelList(List<EventModel> eventModelList);

    EventModel getCurrentEvent();

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
