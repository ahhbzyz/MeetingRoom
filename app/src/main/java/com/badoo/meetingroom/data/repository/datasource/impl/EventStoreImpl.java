package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.data.remote.googlecalendarapi.GoogleCalendarApi;
import com.badoo.meetingroom.data.repository.datasource.intf.EventStore;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
class EventStoreImpl implements EventStore {

    private final GoogleCalendarApi mGoogleApi;

    EventStoreImpl(GoogleCalendarApi googleApi) {
        this.mGoogleApi = googleApi;
    }

    @Override
    public Observable<List<Event>> getEventList(CalendarApiParams params) {
        return this.mGoogleApi.getEventList(params);
    }

    @Override
    public Observable<Event> insertEvent(CalendarApiParams params) {
        return this.mGoogleApi.insertEvent(params);
    }

    @Override
    public Observable<Void> deleteEvent(CalendarApiParams params) {
        return this.mGoogleApi.deleteEvent(params);
    }

    @Override
    public Observable<Event> updateEvent(CalendarApiParams params) {
        return this.mGoogleApi.updateEvent(params);
    }
}