package com.badoo.meetingroom.data.repository.datasource.impl;

import com.badoo.meetingroom.data.InsertEventParams;
import com.badoo.meetingroom.data.GetEventsParams;
import com.badoo.meetingroom.data.remote.GoogleCalendarApi;
import com.badoo.meetingroom.data.repository.datasource.intf.RoomEventDataStore;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */
class RemoteRoomEventDataStore implements RoomEventDataStore {

    private final GoogleCalendarApi mGoogleApi;

    RemoteRoomEventDataStore(GoogleCalendarApi googleApi) {
        this.mGoogleApi = googleApi;
    }

    @Override
    public Observable<List<Event>> getEventList(GetEventsParams params) {
        return this.mGoogleApi.getEventList(params);
    }

    @Override
    public Observable<Event> insertEvent(InsertEventParams params) {
        return this.mGoogleApi.insertEvent(params);
    }
}