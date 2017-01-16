package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface RoomEventRepo {
    Observable<List<RoomEvent>> getRoomEventList(Event event);
    Observable<Event> insertEvent(Event event);
    Observable<Void> deleteEvent(Event event);
    Observable<Event> updateEvent(Event event);
}
