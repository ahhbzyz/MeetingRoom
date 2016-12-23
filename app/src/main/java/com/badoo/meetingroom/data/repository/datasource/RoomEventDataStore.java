package com.badoo.meetingroom.data.repository.datasource;

import com.badoo.meetingroom.data.EventParams;
import com.badoo.meetingroom.data.EventsParams;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface RoomEventDataStore {
    Observable<List<Event>> getEventList(EventsParams params);
    Observable<Event> insertEvent(EventParams params);
}
