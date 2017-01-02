package com.badoo.meetingroom.data.repository.datasource.intf;

import com.badoo.meetingroom.data.InsertEventParams;
import com.badoo.meetingroom.data.GetEventsParams;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface RoomEventDataStore {
    Observable<List<Event>> getEventList(GetEventsParams params);
    Observable<Event> insertEvent(InsertEventParams params);
}
