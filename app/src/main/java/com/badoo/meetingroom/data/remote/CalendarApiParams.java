package com.badoo.meetingroom.data.remote;

import com.google.api.services.calendar.model.Event;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 17/01/2017.
 */

public class CalendarApiParams {

    private String calendarId;
    private String roomName;
    private Event eventParams;

    @Inject
    public CalendarApiParams(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public Event getEventParams() {
        return eventParams;
    }

    public CalendarApiParams setEventParams(Event eventParams) {
        this.eventParams = eventParams;
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
