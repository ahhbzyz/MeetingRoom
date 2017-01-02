package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.data.InsertEventParams;
import com.badoo.meetingroom.data.GetEventsParams;
import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface RoomEventRepository {
    Observable<List<RoomEvent>> getRoomEventList(GetEventsParams params);
    Observable<Event> insertEvent(InsertEventParams params);
}
