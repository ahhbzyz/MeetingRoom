package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;


import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

class GetCalendarListApiCall implements Callable<List<CalendarListEntry>> {

    private Calendar mServices = null;

    private GetCalendarListApiCall(Calendar services) {
        this.mServices = services;
    }

    static GetCalendarListApiCall createGET(Calendar services) {
        return new GetCalendarListApiCall(services);
    }

    public List<CalendarListEntry> requestSyncCall() throws Exception {
        return connectToApi();
    }

    private List<CalendarListEntry> connectToApi() throws Exception {
        CalendarList calendarList = mServices.calendarList().list().execute();
        return calendarList.getItems();
    }

    @Override
    public List<CalendarListEntry> call() throws Exception {
        return requestSyncCall();
    }
}
