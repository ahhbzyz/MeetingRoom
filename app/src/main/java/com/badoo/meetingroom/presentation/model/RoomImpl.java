package com.badoo.meetingroom.presentation.model;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomImpl implements Room {

    private String name;
    private int floor;
    private int status;
    private RoomEventModel currentEvent;
    private int capacity;
    private boolean isTvSupported;
    private boolean isVideoConferenceSupported;
    private boolean isBeverageSupported;
    private boolean isStationerySupported;

    public RoomImpl() {

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
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public RoomEventModel getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public void setCurrentEvent(RoomEventModel currentEvent) {
        this.currentEvent = currentEvent;
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
    public boolean isBeverageSupported() {
        return isBeverageSupported;
    }

    @Override
    public void setBeverageSupported(boolean beverageSupported) {
        isBeverageSupported = beverageSupported;
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
