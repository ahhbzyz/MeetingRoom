package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.data.EventParams;
import com.badoo.meetingroom.data.EventsParams;
import com.badoo.meetingroom.domain.entity.RoomEvent;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface RoomEventRepository {
    Observable<List<RoomEvent>> getRoomEventList(EventsParams params);
    Observable<Event> insertUserEvent(EventParams params);
}
