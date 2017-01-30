package com.badoo.meetingroom.presentation.model.impl;

import android.os.Parcel;

import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class RoomModelImpl implements RoomModel {

    private String id;
    private String name;
    private int floor;
    private int status;
    private int capacity;
    private boolean isTvSupported;
    private boolean isVideoConferenceSupported;
    private boolean isBeverageAllowed;
    private boolean isStationerySupported;
    private List<EventModel> eventModelList;

    public RoomModelImpl() {

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
    public List<EventModel> getEventModelList() {
        return eventModelList;
    }

    @Override
    public void setEventModelList(List<EventModel> eventModelList) {
        this.eventModelList = eventModelList;
    }

    @Override
    public EventModel getCurrentEvent() {
        if (getEventModelList() == null) {
            return null;
        }

        for (EventModel eventModel : getEventModelList()) {
            if (eventModel.isProcessing()) {
                return eventModel;
            }
        }
        return getEventModelList().get(getEventModelList().size() - 1);
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

    public boolean isBeverageAllowed() {
        return isBeverageAllowed;
    }

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.floor);
        dest.writeInt(this.status);
        dest.writeInt(this.capacity);
        dest.writeByte(this.isTvSupported ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideoConferenceSupported ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isBeverageAllowed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isStationerySupported ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.eventModelList);
    }

    private RoomModelImpl(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.floor = in.readInt();
        this.status = in.readInt();
        this.capacity = in.readInt();
        this.isTvSupported = in.readByte() != 0;
        this.isVideoConferenceSupported = in.readByte() != 0;
        this.isBeverageAllowed = in.readByte() != 0;
        this.isStationerySupported = in.readByte() != 0;
        this.eventModelList = in.createTypedArrayList(EventModelImpl.CREATOR);
    }

    public static final Creator<RoomModelImpl> CREATOR = new Creator<RoomModelImpl>() {
        @Override
        public RoomModelImpl createFromParcel(Parcel source) {
            return new RoomModelImpl(source);
        }

        @Override
        public RoomModelImpl[] newArray(int size) {
            return new RoomModelImpl[size];
        }
    };
}
