package com.badoo.meetingroom.presentation.model;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class Room {

    private String name;
    private int floor;
    private int status;
    private RoomEventModel currentEvent;
    private int capacity;
    private boolean isTvSupported;
    private boolean isVideoConferenceSupported;
    private boolean isBeverageSupported;
    private boolean isStationerySupported;

    public Room() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RoomEventModel getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(RoomEventModel currentEvent) {
        this.currentEvent = currentEvent;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isTvSupported() {
        return isTvSupported;
    }

    public void setTvSupported(boolean tvSupported) {
        isTvSupported = tvSupported;
    }

    public boolean isVideoConferenceSupported() {
        return isVideoConferenceSupported;
    }

    public void setVideoConferenceSupported(boolean videoConferenceSupported) {
        isVideoConferenceSupported = videoConferenceSupported;
    }

    public boolean isBeverageSupported() {
        return isBeverageSupported;
    }

    public void setBeverageSupported(boolean beverageSupported) {
        isBeverageSupported = beverageSupported;
    }

    public boolean isStationerySupported() {
        return isStationerySupported;
    }

    public void setStationerySupported(boolean stationerySupported) {
        isStationerySupported = stationerySupported;
    }
}
