package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApi;
import com.badoo.meetingroom.data.repository.datasource.intf.CalendarApiStore;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
class CalendarApiStoreImpl implements CalendarApiStore {

    private final GoogleCalendarApi mGoogleCalendarApi;

    CalendarApiStoreImpl(GoogleCalendarApi googleApi) {
        this.mGoogleCalendarApi = googleApi;
    }

    @Override
    public Observable<List<Event>> getEventList(CalendarApiParams params) {
        return this.mGoogleCalendarApi.getEventList(params);
    }

    @Override
    public Observable<Event> insertEvent(CalendarApiParams params) {
        return this.mGoogleCalendarApi.insertEvent(params);
    }

    @Override
    public Observable<Void> deleteEvent(CalendarApiParams params) {
        return this.mGoogleCalendarApi.deleteEvent(params);
    }

    @Override
    public Observable<Event> updateEvent(CalendarApiParams params) {
        return this.mGoogleCalendarApi.updateEvent(params);
    }

    @Override
    public Observable<List<CalendarListEntry>> getCalendarList() {
        return mGoogleCalendarApi.getCalendarList();
    }

    @Override
    public Observable<Void> bindPushNotifications(CalendarApiParams params) {
        return mGoogleCalendarApi.bindPushNotifications(params);
    }
}