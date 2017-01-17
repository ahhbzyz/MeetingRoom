package com.badoo.meetingroom.data.repository.datasource.impl;

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
    public Observable<List<Event>> getEventList(Event event) {
        return this.mGoogleApi.getEventList(event);
    }

    @Override
    public Observable<Event> insertEvent(Event event) {
        return this.mGoogleApi.insertEvent(event);
    }

    @Override
    public Observable<Void> deleteEvent(Event event) {
        return this.mGoogleApi.deleteEvent(event);
    }

    @Override
    public Observable<Event> updateEvent(Event event) {
        return this.mGoogleApi.updateEvent(event);
    }
}