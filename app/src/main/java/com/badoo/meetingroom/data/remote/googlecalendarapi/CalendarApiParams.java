package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.model.Event;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 17/01/2017.
 */

public class CalendarApiParams {

    private String calendarId;
    private Event eventParams;
    private int numOfResult = 250;

    @Inject
    public CalendarApiParams(String calendarId) {
        this.calendarId = calendarId;
    }

    String getCalendarId() {
        return calendarId;
    }

    Event getEventParams() {
        return eventParams;
    }

    public CalendarApiParams setEventParams(Event eventParams) {
        this.eventParams = eventParams;
        return this;
    }

    int getNumOfResult() {
        return numOfResult;
    }

    // todo check
    public CalendarApiParams setNumOfResult(int numOfResult) {
        this.numOfResult = numOfResult;
        return this;
    }
}
