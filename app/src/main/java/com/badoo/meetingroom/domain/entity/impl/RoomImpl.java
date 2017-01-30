package com.badoo.meetingroom.domain.entity.impl;

import com.badoo.meetingroom.domain.entity.intf.Room;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomImpl implements Room {

    private String id;
    private String name;
    private int floor;
    private int capacity;
    private boolean isTvSupported;
    private boolean isVideoConferenceSupported;
    private boolean isBeverageAllowed;
    private boolean isStationerySupported;

    public RoomImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getFloor() {
        return floor;
    }

    @Override
    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean isTvSupported() {
        return isTvSupported;
    }

    @Override
    public void setTvSupported(boolean tvSupported) {
        isTvSupported = tvSupported;
    }

    @Override
    public boolean isVideoConferenceSupported() {
        return isVideoConferenceSupported;
    }

    @Override
    public void setVideoConferenceSupported(boolean videoConferenceSupported) {
        isVideoConferenceSupported = videoConferenceSupported;
    }

    @Override
    public boolean isBeverageAllowed() {
        return isBeverageAllowed;
    }

    @Override
    public void setBeverageAllowed(boolean beverageAllowed) {
        isBeverageAllowed = beverageAllowed;
    }

    @Override
    public boolean isStationerySupported() {
        return isStationerySupported;
    }

    @Override
    public void setStationerySupported(boolean stationerySupported) {
        isStationerySupported = stationerySupported;
    }
}
