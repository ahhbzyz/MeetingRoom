package com.badoo.meetingroom.data.repository.datasource;

import com.badoo.meetingroom.data.EventParams;
import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.data.remote.GoogleCalendarApi;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
public class RemoteRoomEventDataStore implements RoomEventDataStore {

    private final GoogleCalendarApi mGoogleApi;

    RemoteRoomEventDataStore(GoogleCalendarApi googleApi) {
        this.mGoogleApi = googleApi;
    }

    @Override
    public Observable<List<Event>> getEventList(EventsParams params) {
        return this.mGoogleApi.getEventList(params);
    }

    @Override
    public Observable<Event> insertEvent(EventParams params) {
        return null;
    }
}