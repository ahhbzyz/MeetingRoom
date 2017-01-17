package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.domain.entity.intf.Room;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public interface CalendarListRepo {
    Observable<List<Room>> getCalendarList();
}
